package inuverse.example.model

interface Vector {
    val size: Int

    operator fun get(i: Int): Double

    fun add(other: Vector): Vector

    fun subtract(other: Vector): Vector

    fun scale(scale: Double): Vector

    fun dot(other: Vector): Double
    fun cross(other: Vector): Vector
    fun norm(): Double
    fun normalize(): Vector
}