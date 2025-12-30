package inuverse.mnist.neural.layer

import inuverse.mnist.model.Vector
import inuverse.mnist.model.DenseVector
import kotlin.math.max

class ReLU: Layer {
    private lateinit var input: Vector // 逆伝播で勾配をマスクするために順伝播時の入力を保存

    override fun forward(input: Vector): Vector {
        this.input = input
        val result = DoubleArray(input.size) { i ->
            max(0.0, input[i])
        }
        return DenseVector(input.size, result)
    }

    override fun backward(outputGradient: Vector): Vector {
        val result = DoubleArray(outputGradient.size) { i ->
            // 順伝播時の入力が0より大きい要素だけ、上流からの勾配をそのまま流す
            if (input[i] > 0.0) outputGradient[i] else 0.0
        }
        return DenseVector(outputGradient.size, result)
    }
}