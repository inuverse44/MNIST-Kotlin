package inuverse.mnist.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inuverse.mnist.constants.MnistConst
import inuverse.mnist.repository.DataLoadContext
import inuverse.mnist.repository.MnistImageLoadStrategyImpl
import inuverse.mnist.repository.MnistLabelLoadStrategyImpl
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent
import inuverse.mnist.service.ModelLoader
import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.spec.LayerEntry
import inuverse.mnist.neural.spec.LayerFactory
import inuverse.mnist.neural.spec.ModelSpec
import inuverse.mnist.service.MnistDatasetService
import inuverse.mnist.service.MnistTrainer
import inuverse.mnist.service.ModelSaver
import inuverse.mnist.model.TrainingConfig
import java.io.File
import org.slf4j.LoggerFactory
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class MnistServer(private val modelPath: String) {

    private val logger = LoggerFactory.getLogger(MnistServer::class.java)
    private val json = jacksonObjectMapper()
    private val networkRef: AtomicReference<Network> = AtomicReference()
    private val executor = Executors.newSingleThreadExecutor()
    private val jobs = ConcurrentHashMap<String, TrainJobStatus>()

    fun start() {
        // Cloud Run sets the PORT environment variable
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

        val network = loadOrCreateNetwork()
        networkRef.set(network)

        logger.info("🚀 Starting server on http://0.0.0.0:$port")
        embeddedServer(Netty, port = port, host = "0.0.0.0") {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }
            
            routing {
                staticResources("/", "static", index = "index.html")

                get("/healthz") {
                    call.respondText("ok")
                }

                get("/version") {
                    val modelFile = File(modelPath)
                    val exists = modelFile.exists()
                    val info = mutableMapOf<String, Any>(
                        "modelPath" to modelPath,
                        "exists" to exists
                    )
                    if (exists) {
                        info["size"] = modelFile.length()
                        runCatching {
                            val tree = json.readTree(modelFile)
                            if (tree.has("version")) {
                                info["modelSpecVersion"] = tree.get("version").asText()
                            }
                        }
                    }
                    call.respond(info)
                }
                
                post("/api/predict") {
                    val req = call.receive<PredictRequest>()
                    
                    if (req.image.size != MnistConst.MNIST_INPUT_SIZE) {
                        call.respond(mapOf("error" to "Image must be ${MnistConst.MNIST_INPUT_SIZE} pixels. Received: ${req.image.size}"))
                        return@post
                    }
                    
                    val net = networkRef.get()
                    val inputVector = DenseVector(MnistConst.MNIST_INPUT_SIZE, req.image.toDoubleArray())
                    val outputVector = net.predict(inputVector)
                    
                    val probabilities = outputVector.getData().toList()
                    val predictedDigit = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                    
                    call.respond(PredictResponse(predictedDigit, probabilities))
                }

                // Start training asynchronously based on provided layers/config
                post("/api/train/start") {
                    val req = call.receive<TrainStartRequest>()
                    val jobId = UUID.randomUUID().toString()
                    jobs[jobId] = TrainJobStatus(jobId, state = "queued")
                    executor.submit {
                        try {
                            jobs[jobId] = TrainJobStatus(jobId, state = "running")
                            val newNet = runTrainingJob(req) { epoch, loss, acc ->
                                jobs[jobId] = TrainJobStatus(jobId, state = "running", epoch = epoch, loss = loss, accuracy = acc)
                            }
                            // Save model and hot-swap
                            ModelSaver().save(newNet, req.save?.path ?: modelPath)
                            networkRef.set(newNet)
                            jobs[jobId] = TrainJobStatus(jobId, state = "completed")
                        } catch (e: Exception) {
                            logger.error("Training job failed: ${e.message}", e)
                            jobs[jobId] = TrainJobStatus(jobId, state = "failed", message = e.message)
                        }
                    }
                    call.respond(mapOf("jobId" to jobId))
                }

                get("/api/train/status") {
                    val jobId = call.request.queryParameters["jobId"]
                    if (jobId == null) {
                        call.respond(mapOf("error" to "jobId is required"))
                        return@get
                    }
                    val status = jobs[jobId]
                    if (status == null) {
                        call.respond(mapOf("error" to "job not found"))
                        return@get
                    }
                    call.respond(status)
                }
            }
        }.start(wait = true)
    }

    private fun createNetwork(): Network {
        // Must match the architecture used during training (Main.kt)
        val network = Network(
            loss = CrossEntropy(),
            optimizer = StochasticGradientDescent(0.01) // Not used for inference
        )
        network.add(Dense(MnistConst.MNIST_INPUT_SIZE, 100))
        network.add(ReLU())
        network.add(Dense(100, 10))
        network.add(Softmax())
        return network
    }

    private fun loadOrCreateNetwork(): Network {
        val file = File(modelPath)
        if (file.exists()) {
            // 新形式の読込を試み、失敗したらデフォルト構成に旧形式の読込を試行
            return kotlin.runCatching {
                ModelLoader().loadToNewNetwork(modelPath, learningRate = 0.01)
            }.getOrElse { specError ->
                logger.warn("Failed to load spec model: ${specError.message}. Falling back to default architecture.")
                val fallback = createNetwork()
                kotlin.runCatching {
                    ModelLoader().load(modelPath, fallback)
                }.onFailure { legacyError ->
                    logger.warn("Failed to load legacy model: ${legacyError.message}")
                }
                fallback
            }
        }
        logger.warn("Model file not found at $modelPath. Using random weights (predictions will be random).")
        return createNetwork()
    }

    private fun runTrainingJob(
        req: TrainStartRequest,
        onProgress: (epoch: Int, loss: Double, accuracy: Double) -> Unit
    ): Network {
        // 1. Load dataset
        val baseDir = if (File("app").exists()) "app/" else ""
        val images = DataLoadContext(MnistImageLoadStrategyImpl()).load("${baseDir}t10k-images.idx3-ubyte")
        val labels = DataLoadContext(MnistLabelLoadStrategyImpl()).load("${baseDir}t10k-labels.idx1-ubyte")
        val datasetService = MnistDatasetService(images, labels)

        // 2. Build network
        val network = if (req.layers != null && req.layers.isNotEmpty()) {
            LayerFactory.buildNetwork(ModelSpec(version = "1", layers = req.layers), learningRate = req.config.learningRate)
        } else {
            val net = Network(CrossEntropy(), StochasticGradientDescent(req.config.learningRate))
            net.add(Dense(MnistConst.MNIST_INPUT_SIZE, req.config.hiddenLayerSize ?: 100))
            net.add(ReLU())
            net.add(Dense(req.config.hiddenLayerSize ?: 100, 10))
            net.add(Softmax())
            net
        }

        // 3. Train
        val all = datasetService.getAllDataset().shuffled()
        val train = all.take(req.config.trainSize)
        val test = all.drop(req.config.trainSize).take(req.config.testSize)
        val trainer = MnistTrainer(network, train, test)
        val history = trainer.train(req.config.epochs)
        history.forEach { onProgress(it.epoch, it.loss, it.accuracy) }

        return network
    }
}

data class TrainStartRequest(
    val layers: List<LayerEntry>?,
    val config: TrainConfigReq,
    val save: SaveReq? = null
)

data class TrainConfigReq(
    val trainSize: Int = 5000,
    val testSize: Int = 1000,
    val epochs: Int = 10,
    val learningRate: Double = 0.01,
    val hiddenLayerSize: Int? = 100
)

data class SaveReq(
    val path: String? = null
)

data class TrainJobStatus(
    val jobId: String,
    val state: String,
    val epoch: Int? = null,
    val loss: Double? = null,
    val accuracy: Double? = null,
    val message: String? = null
)
