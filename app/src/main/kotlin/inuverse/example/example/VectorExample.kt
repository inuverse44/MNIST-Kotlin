package inuverse.example.example

import inuverse.example.model.DenseVector

class VectorExample {
    fun run() {
        val vecData1 = doubleArrayOf(1.0, 2.0, 3.0)
        val vec1 = DenseVector(vecData1.size, vecData1)

        val vecData2 = doubleArrayOf(1.0, 0.0, 2.0)
        val vec2 = DenseVector(vecData2.size, vecData2)

        val scaleResult = vec1.scale(2.0)
        val dotResult = vec1.dot(vec2)
        val normResult = vec1.norm()
        val normalizeResult = vec1.normalize()

        println("vec1: $vec1")
        println("vec2: $vec2")
        println("vec1 + vec2: ${vec1.add(vec2)}")
        println("vec1 - vec2: ${vec1.subtract(vec2)}")
        println("scaleResult: $scaleResult")
        println("dotResult: $dotResult")
        println("normResult: $normResult")
        println("normalizeResult: $normalizeResult")
    }
}