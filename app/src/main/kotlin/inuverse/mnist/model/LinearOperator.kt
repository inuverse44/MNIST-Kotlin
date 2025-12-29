package inuverse.mnist.model

interface LinearOperator {
    val inputDim: Int
    val outputDim: Int

    // 線形演算の抽象化 A: x -> Ax
    fun apply(x: Vector): Vector
}