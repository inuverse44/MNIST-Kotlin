package inuverse.example.model

interface Matrix : LinearOperator {
    val rows: Int
    val cols: Int

    operator fun get(i: Int, j: Int): Double
    fun add(other: Matrix): Matrix
    fun mul(other: Matrix): Matrix
    fun transpose(): Matrix
}

