package inuverse.example.neural.layer

import kotlin.math.exp
import inuverse.example.model.Vector
import inuverse.example.model.DenseVector

class Sigmoid: Layer {
    private lateinit var output: Vector // Backwardで使うために出力を保存する

    /**
     * u^(l) = W^(l) z^(l-1) + b^(l)
     * z^(l) = \sigma^(l)(u^(l))
     * というのが全結合ニューラルネット。ここでは\sigma^(l)がsigmoid function
     * である
     */
    override fun forward(input: Vector): Vector {
        val result = DoubleArray(input.size) { i ->
            sigmoid(input[i])
        }

        val outputVector = DenseVector(input.size, result)

        // backwardのために保存しておく
        this.output = outputVector
        return outputVector
    }

    /**
     * outputGradient: dLoss/dy（出力側からの勾配）
     * this.output: y （純伝播の時の出力）
     */
    override fun backward(outputGradient: Vector): Vector {
        val result = DoubleArray(outputGradient.size) { i ->
            val y = output[i] // 保存しておいたやつやで
            outputGradient[i] * y * (1.0 - y)
        }

        return DenseVector(outputGradient.size, result)
    }

    private fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))
}