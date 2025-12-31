package inuverse.mnist.server.dto

data class PredictResponse(
    val digit: Int,
    val probabilities: List<Double>
)
