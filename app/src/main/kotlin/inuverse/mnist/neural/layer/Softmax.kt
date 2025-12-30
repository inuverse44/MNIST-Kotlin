package inuverse.mnist.neural.layer

import inuverse.mnist.model.DenseVector
import inuverse.mnist.model.Vector
import kotlin.math.exp

class Softmax : Layer {
    private lateinit var output: Vector

    override fun forward(input: Vector): Vector {
        // expのオーバーフロー防止
        var max = input[0]
        for (i in 1 until input.size) {
            if (input[i] > max) max = input[i]
        }

        val exps = DoubleArray(input.size) { i -> exp(input[i] - max) }
        val sum = exps.sum()
        
        val result = DoubleArray(input.size) { i -> exps[i] / sum }
        this.output = DenseVector(input.size, result)
        return this.output
    }

    /**
     * Softmaxの逆伝播
     * dL/dx_i = y_i * (dL/dy_i - sum_j(y_j * dL/dy_j))
     */
    override fun backward(outputGradient: Vector): Vector {
        val n = output.size
        val dotProduct = output.dot(outputGradient)

        val result = DoubleArray(n) { i ->
            output[i] * (outputGradient[i] - dotProduct)
        }
        return DenseVector(n, result)
    }
}
