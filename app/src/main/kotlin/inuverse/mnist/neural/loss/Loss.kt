package inuverse.mnist.neural.loss

import inuverse.mnist.model.Vector

interface Loss {
    fun forward(predicted: Vector, actual: Vector): Double
    fun backward(predicted: Vector, actual: Vector): Vector
}