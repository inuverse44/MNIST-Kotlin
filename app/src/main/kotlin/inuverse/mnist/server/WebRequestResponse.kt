package inuverse.mnist.server

data class PredictRequest(
    val image: List<Double>
)

data class PredictResponse(
    val digit: Int,
    val probabilities: List<Double>
)
