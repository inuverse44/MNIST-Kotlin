package inuverse.example.neural.layer

import inuverse.example.model.Vector
import inuverse.example.model.Matrix
import inuverse.example.model.DenseVector
import inuverse.example.model.DenseMatrix
import kotlin.random.Random

class Dense(
    val inputSize: Int,
    val outputSize: Int
): Layer {

    /**
     * 📝
     * 初期化するときにはランダムでハイパーパラメタ（重みとバイアスに値を与える）
     * \vec{xNext} = W \vec{x} + \vec{b}
     * まずは重みから。-1.0 ~ 1.0
     */
    var weights: Matrix = DenseMatrix(
        outputSize,
        inputSize,
        DoubleArray(outputSize * inputSize) { Random.nextDouble() * 2 - 1 }
        )

    var bias: Vector = DenseVector(outputSize, DoubleArray(outputSize))

    // キャッシュ
    private lateinit var input: Vector

    // 勾配（学習用）
    lateinit var weightsGradient: Matrix
    lateinit var biasGradient: Vector

    override fun forward(input: Vector): Vector {
        this.input = input
        // y = Wx + b
        return weights.apply(input).add(bias)
    }

    override fun backward(outputGradient: Vector): Vector {
        // 重みの勾配 dL/dW = dL/dY * x^T
        weightsGradient = outputGradient.outerProduct(input)

        // バイアスの勾配　dL/db = dL/dy
        biasGradient = outputGradient

        // 入力への勾配 dL/dx = W^T * dL/dy
        return weights.transpose().apply(outputGradient)
    }

}