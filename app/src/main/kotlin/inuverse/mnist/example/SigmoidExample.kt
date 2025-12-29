package inuverse.mnist.example

import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.layer.Sigmoid

class SigmoidExample {
    fun run() {
        val vecData = doubleArrayOf(1.0, 2.0, 3.0)
        val vec = DenseVector(3, vecData)
        println("vec: $vec")


        val sigmoidLayer = Sigmoid()
        val outputVec = sigmoidLayer.forward(vec)
        println("outputVec: $outputVec")

        val backwardVec = sigmoidLayer.backward(vec)
        println("backwardVec: $backwardVec")
    }
}