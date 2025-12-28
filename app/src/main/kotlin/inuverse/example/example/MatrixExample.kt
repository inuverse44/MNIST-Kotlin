package inuverse.example.example

import inuverse.example.model.DenseMatrix

class MatrixExample {
    fun example() {
        val rows = 2
        val cols = 2

        val dataA1 = doubleArrayOf(
            1.0, 1.0,
            0.0, 1.0
        )
        val a1 = DenseMatrix(rows, cols, dataA1)

        val dataA2 = doubleArrayOf(
            0.5, 0.5,
            0.5, 0.5
        )

        val a2 = DenseMatrix(rows, cols, dataA2)

        val a3 = a1.add(a2)
        val a4 = a1.mul(a2)

        val dataA5 = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val a5 = DenseMatrix(3, 2, dataA5)
        val a6 = a5.transpose()
        println("A1: $a1 A2: $a2 A3: $a3 A4: $a4 A5: $a5 A6: $a6")
    }
}