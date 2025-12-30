package inuverse.mnist.model

/**
 * m x n Matrix
 */
interface Matrix {
    val rows: Int
    val cols: Int
    val inputDim: Int
    val outputDim: Int

    operator fun get(i: Int, j: Int): Double
    fun apply(x: Vector): Vector
    fun add(other: Matrix): Matrix
    fun subtract(other: Matrix): Matrix
    fun mul(other: Matrix): Matrix
    fun scale(scale: Double): Matrix
    fun transpose(): Matrix
    
    // 内部データを取得する (シリアライズ用)
    fun getData(): DoubleArray
}

