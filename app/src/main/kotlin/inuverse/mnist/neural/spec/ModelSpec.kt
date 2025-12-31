package inuverse.mnist.neural.spec

/**
 * JSONに保存/読み込みするためのモデル仕様。
 * 例:
 * {
 *   "version": "1",
 *   "layers": [
 *     {"type":"Dense", "inputSize":784, "outputSize":100, "weights":[...], "biases":[...]},
 *     {"type":"ReLU"},
 *     {"type":"Dense", "inputSize":100, "outputSize":10, "weights":[...], "biases":[...]},
 *     {"type":"Softmax"}
 *   ]
 * }
 */
data class ModelSpec(
    val version: String = "1",
    val layers: List<LayerEntry>
)

data class LayerEntry(
    val type: String,
    val inputSize: Int? = null,
    val outputSize: Int? = null,
    val weights: DoubleArray? = null,
    val biases: DoubleArray? = null
)

