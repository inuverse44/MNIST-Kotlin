package inuverse.mnist.server

import com.fasterxml.jackson.databind.SerializationFeature
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
import java.io.File

class MnistServer(private val modelPath: String) {

    fun start() {
        // Cloud Run sets the PORT environment variable
        val port = System.getenv("PORT")?.toIntOrNull() ?: 8080

        val network = loadOrCreateNetwork()

        println("🚀 Starting server on http://0.0.0.0:$port")
        embeddedServer(Netty, port = port, host = "0.0.0.0") {
            install(ContentNegotiation) {
                jackson {
                    enable(SerializationFeature.INDENT_OUTPUT)
                }
            }
            
            routing {
                staticResources("/", "static", index = "index.html")
                
                post("/api/predict") {
                    val req = call.receive<PredictRequest>()
                    
                    if (req.image.size != MnistConst.Mnist1DLength) {
                        call.respond(mapOf("error" to "Image must be 784 pixels. Received: ${req.image.size}"))
                        return@post
                    }
                    
                    val inputVector = DenseVector(MnistConst.Mnist1DLength, req.image.toDoubleArray())
                    val outputVector = network.predict(inputVector)
                    
                    val probabilities = outputVector.getData().toList()
                    val predictedDigit = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
                    
                    call.respond(PredictResponse(predictedDigit, probabilities))
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
        network.add(Dense(MnistConst.Mnist1DLength, 100))
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
                println("🐶Failed to load spec model: ${specError.message}. Falling back to default architecture.")
                val fallback = createNetwork()
                kotlin.runCatching {
                    ModelLoader().load(modelPath, fallback)
                }.onFailure { legacyError ->
                    println("🐶Failed to load legacy model: ${legacyError.message}")
                }
                fallback
            }
        }
        println("🐶Model file not found at $modelPath. Using random weights (predictions will be random).")
        return createNetwork()
    }
}
