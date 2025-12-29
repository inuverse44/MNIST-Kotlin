package inuverse.example.neural.loss

import inuverse.example.model.Vector

interface Loss {
    fun forward(predicted: Vector, actual: Vector): Double
    fun backward(predicted: Vector, actual: Vector): Vector
}