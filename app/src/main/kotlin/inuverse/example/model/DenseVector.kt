package inuverse.example.model

import kotlin.math.sqrt

class DenseVector(
    override val size: Int,
    private val data: DoubleArray
): Vector {
    override fun get(i: Int): Double {
        return data[i]
    }

    override fun add(other: Vector): Vector {
        val result = DoubleArray(size)
        for (i in 0 until size) {
            result[i] = this[i] + other[i]
        }
        return DenseVector(size, result)
    }

    override fun substract(other: Vector): Vector {
        val result = DoubleArray(size)
        for (i in 0 until size) {
            result[i] = this[i] - other[i]
        }
        return DenseVector(size, result)
    }

    /**
     * 定数倍
     */
    override fun scale(scale: Double): Vector {
        val result = DoubleArray(size)
        for (i in 0 until size) {
            result[i] = scale * data[i]
        }
        return DenseVector(size, result)
    }

    /**
     * 内積の計算
     */
    override fun dot(other: Vector): Double {
        require(size == other.size) {
            "Dimension mismatch: vector size $size and ${other.size}"
        }
        val result: Double
        var sum = 0.0
        for (i in 0 until size) {
            sum += data[i] * other[i]
        }
        result = sum
        return result
    }

    override fun cross(other: Vector): Vector {
        TODO("Not yet implemented")
    }

    /**
     * ノルム
     * norm(x) = sqrt(x.dot(x))
     */
    override fun norm(): Double {
        val ownVector = DenseVector(size, data)
        val squaredNorm = this.dot(ownVector)
        val result = sqrt(squaredNorm)
        return result
    }

    /**
     * 規格化
     */
    override fun normalize(): Vector {
        val norm = this.norm()
        val result = this.scale(1.0/norm)
        return result
    }

    override fun toString(): String {
        return data.joinToString(", ", "[", "]")
    }

}