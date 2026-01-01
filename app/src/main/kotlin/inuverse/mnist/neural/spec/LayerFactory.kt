package inuverse.mnist.neural.spec

import inuverse.mnist.model.DenseMatrix
import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Sigmoid
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent

object LayerFactory {
    /**
     * ModelSpec から Network を動的に構築する。
     * 学習率は推論では使用しないが、Network要件のため指定する。
     */
    fun buildNetwork(spec: ModelSpec, learningRate: Double = 0.01): Network {
        val network = Network(
            loss = CrossEntropy(),
            optimizer = StochasticGradientDescent(learningRate)
        )

        var currentDim: Int? = null
        var lastDenseOut: Int? = null

        for ((idx, entry) in spec.layers.withIndex()) {
            when (entry.type) {
                "Dense" -> {
                    val inSize = requireNotNull(entry.inputSize) { "Dense.inputSize is required" }
                    val outSize = requireNotNull(entry.outputSize) { "Dense.outputSize is required" }

                    if (currentDim != null) {
                        require(inSize == currentDim) {
                            "Dense.inputSize mismatch at layer $idx: expected $currentDim, got $inSize"
                        }
                    }
                    val dense = Dense(inSize, outSize)

                    // パラメタが与えられていれば適用。なければDenseが持つ乱数初期化を使用して学習に備える。
                    val w = entry.weights
                    val b = entry.biases
                    if (w != null && b != null) {
                        require(w.size == outSize * inSize) { "Weights size mismatch: expected ${outSize * inSize}, got ${w.size}" }
                        require(b.size == outSize) { "Bias size mismatch: expected $outSize, got ${b.size}" }
                        dense.weights = DenseMatrix(outSize, inSize, w)
                        dense.bias = DenseVector(outSize, b)
                    }

                    network.add(dense)
                    currentDim = outSize
                    lastDenseOut = outSize
                }
                "ReLU" -> network.add(ReLU())
                "Softmax" -> network.add(Softmax())
                "Sigmoid" -> network.add(Sigmoid())
                else -> throw IllegalArgumentException("Unsupported layer type: ${entry.type}")
            }
            // 活性化関数は次元を変えないためcurrentDimを維持。最初に活性が来るのは不正。
            if (entry.type == "ReLU" || entry.type == "Softmax" || entry.type == "Sigmoid") {
                require(currentDim != null) { "Activation layer cannot be the first layer (index $idx)" }
            }
        }

        return network
    }
}
