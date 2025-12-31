package inuverse.mnist.server

data class TrainConfigReq(
    val trainSize: Int = 5000,
    val testSize: Int = 1000,
    val epochs: Int = 10,
    val learningRate: Double = 0.01,
    val hiddenLayerSize: Int? = 100
)

