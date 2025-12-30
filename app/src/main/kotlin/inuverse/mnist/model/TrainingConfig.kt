package inuverse.mnist.model

/**
 * 学習の設定を保持するデータクラス
 */
data class TrainingConfig(
    val trainSize: Int = 5000,
    val testSize: Int = 1000,
    val epochs: Int = 50,
    val learningRate: Double = 0.01,
    val hiddenLayerSize: Int = 100,
    val randomSeed: Long = 123L
)
