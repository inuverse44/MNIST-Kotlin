package inuverse.mnist.model

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DenseVectorTest {
    @Test
    fun dot_outer_normalize() {
        val v = DenseVector(3, doubleArrayOf(3.0, 4.0, 0.0))
        val norm = v.norm()
        assertEquals(5.0, norm, 1e-12)

        val vn = v.normalize()
        assertEquals(1.0, vn.norm(), 1e-12)

        val w = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))
        val dot = v.dot(w)
        assertEquals(3.0 * 1.0 + 4.0 * 2.0 + 0.0 * 3.0, dot, 1e-12)

        val m = v.outerProduct(w) // 3x3
        assertEquals(3, m.rows)
        assertEquals(3, m.cols)
        // check a couple entries
        assertEquals(3.0 * 1.0, m[0, 0], 1e-12)
        assertEquals(4.0 * 3.0, m[1, 2], 1e-12)

        // ensure original vector data not mutated
        val data = v.getData()
        assertTrue(abs(data[0] - 3.0) < 1e-12)
    }
}

