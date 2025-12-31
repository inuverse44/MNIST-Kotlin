package inuverse.mnist.server

data class TrainJobStatus(
    val jobId: String,
    val state: String,
    val epoch: Int? = null,
    val loss: Double? = null,
    val accuracy: Double? = null,
    val message: String? = null
)

