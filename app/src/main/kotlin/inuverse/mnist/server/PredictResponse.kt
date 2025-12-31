package inuverse.mnist.server

data class PredictResponse(
    val digit: Int,
    val probabilities: List<Double>
)

