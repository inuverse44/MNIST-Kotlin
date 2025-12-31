package inuverse.mnist.server.core

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inuverse.mnist.constants.MnistConst
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
import inuverse.mnist.server.service.TrainingManager
import java.io.File
import inuverse.mnist.server.routes.healthRoutes
import inuverse.mnist.server.routes.versionRoutes
import inuverse.mnist.server.routes.predictRoutes
import inuverse.mnist.server.routes.trainRoutes
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

class MnistServer(private val modelPath: String) {

    private val logger = LoggerFactory.getLogger(MnistServer::class.java)
    private val json = jacksonObjectMapper()
    private val networkRef: AtomicReference<Network> = AtomicReference()
    private val trainingManager = TrainingManager(modelPath) { net -> networkRef.set(net) }

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
                healthRoutes()
                versionRoutes(modelPath, json)
                predictRoutes(networkRef)
                trainRoutes(trainingManager)
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

}
