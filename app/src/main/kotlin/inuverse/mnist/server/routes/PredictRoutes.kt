package inuverse.mnist.server.routes

import inuverse.mnist.constants.MnistConst
import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.Network
import inuverse.mnist.server.dto.PredictRequest
import inuverse.mnist.server.dto.PredictResponse
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.concurrent.atomic.AtomicReference

fun Routing.predictRoutes(networkRef: AtomicReference<Network>) {
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
}

