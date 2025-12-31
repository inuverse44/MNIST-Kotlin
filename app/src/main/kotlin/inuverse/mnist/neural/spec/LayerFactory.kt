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

        for (entry in spec.layers) {
            when (entry.type) {
                "Dense" -> {
                    val inSize = requireNotNull(entry.inputSize) { "Dense.inputSize is required" }
                    val outSize = requireNotNull(entry.outputSize) { "Dense.outputSize is required" }
                    val dense = Dense(inSize, outSize)

                    // パラメタ適用（推論前提）。weightsは [outSize x inSize] の行優先。
                    val w = requireNotNull(entry.weights) { "Dense.weights is required" }
                    val b = requireNotNull(entry.biases) { "Dense.biases is required" }
                    require(w.size == outSize * inSize) { "Weights size mismatch: expected ${outSize * inSize}, got ${w.size}" }
                    require(b.size == outSize) { "Bias size mismatch: expected $outSize, got ${b.size}" }
                    dense.weights = DenseMatrix(outSize, inSize, w)
                    dense.bias = DenseVector(outSize, b)

                    network.add(dense)
                }
                "ReLU" -> network.add(ReLU())
                "Softmax" -> network.add(Softmax())
                "Sigmoid" -> network.add(Sigmoid())
                else -> throw IllegalArgumentException("Unsupported layer type: ${entry.type}")
            }
        }

        return network
    }
}

