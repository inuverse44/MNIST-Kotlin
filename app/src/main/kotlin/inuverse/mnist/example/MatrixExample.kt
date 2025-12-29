package inuverse.mnist.example

import inuverse.mnist.model.DenseMatrix
import inuverse.mnist.model.DenseVector

class MatrixExample {
    fun run() {
        val rows = 2
        val cols = 2

        val dataA1 = doubleArrayOf(
            1.0, 0.0,
            0.0, 1.0
        )
        val a1 = DenseMatrix(rows, cols, dataA1)
        println("A1: $a1")

        val vecData = doubleArrayOf(1.0, 2.0)
        val vec = DenseVector(vecData.size, vecData)
        val applyedVec = a1.apply(vec)
        println("applyedVec: $applyedVec")


        val dataA2 = doubleArrayOf(
            0.5, 0.5,
            0.5, 0.5
        )

        val a2 = DenseMatrix(rows, cols, dataA2)
        println("A2: $a2")

        val a3 = a1.add(a2)
        println("A3: $a3")

        val a4 = a1.mul(a2)
        println("A4: $a4")

        val dataA5 = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val a5 = DenseMatrix(3, 2, dataA5)
        println("A5: $a5")

        val a6 = a5.transpose()
        println("A6: $a6")


    }
}