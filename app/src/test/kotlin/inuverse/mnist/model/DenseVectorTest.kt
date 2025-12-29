package inuverse.mnist.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class DenseVectorTest {

    private val eps = 1e-9

    @Test
    fun testVectorSize() {
        val v = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))
        assertEquals(3, v.size)
    }

    @Test
    fun testVectorAdd() {
        val v1 = DenseVector(2, doubleArrayOf(1.0, 2.0))
        val v2 = DenseVector(2, doubleArrayOf(3.0, 4.0))
        val result = v1.add(v2)

        assertEquals(4.0, result[0], eps)
        assertEquals(6.0, result[1], eps)
    }

    @Test
    fun testVectorSubtract() {
        val v1 = DenseVector(2, doubleArrayOf(5.0, 7.0))
        val v2 = DenseVector(2, doubleArrayOf(3.0, 4.0))
        val result = v1.subtract(v2)

        assertEquals(2.0, result[0], eps)
        assertEquals(3.0, result[1], eps)
    }

    @Test
    fun testVectorScale() {
        val v = DenseVector(2, doubleArrayOf(1.0, 2.0))
        val result = v.scale(2.5)

        assertEquals(2.5, result[0], eps)
        assertEquals(5.0, result[1], eps)
    }

    @Test
    fun testVectorDot() {
        val v1 = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))
        val v2 = DenseVector(3, doubleArrayOf(4.0, 5.0, 6.0))
        val result = v1.dot(v2)

        assertEquals(32.0, result, eps)
    }

    @Test
    fun testDimensionMismatch() {
        val v1 = DenseVector(2, doubleArrayOf(1.0, 2.0))
        val v2 = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))

        assertFailsWith<IllegalArgumentException> {
            v1.add(v2)
        }
    }

    @Test
    fun testNormAndNormalize() {
        val v = DenseVector(3, doubleArrayOf(3.0, 4.0, 0.0))
        assertEquals(5.0, v.norm(), eps)

        val normalized = v.normalize()
        assertEquals(0.6, normalized[0], eps)
        assertEquals(0.8, normalized[1], eps)
        assertEquals(0.0, normalized[2], eps)
        assertEquals(1.0, normalized.norm(), eps)
    }
}
