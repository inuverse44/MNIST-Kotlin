package inuverse.example.model

interface Vector {
    val size: Int

    operator fun get(i: Int): Double

    fun dot(other: Vector): Double
    fun norm(): Double
}