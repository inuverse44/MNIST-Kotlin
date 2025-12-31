package inuverse.mnist.model

import inuverse.mnist.EPS
import kotlin.test.Test
import kotlin.test.assertEquals

class DenseMatrixTest {
    @Test
    fun apply_and_transpose_and_mul() {
        val a = DenseMatrix(2, 2, doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        ))
        val v = DenseVector(2, doubleArrayOf(1.0, 1.5))
        val av = a.apply(v)
        assertEquals(2, av.size)
        assertEquals(1.0 * 1.0 + 2.0 * 1.5, av[0], EPS)
        assertEquals(3.0 * 1.0 + 4.0 * 1.5, av[1], EPS)

        val at = a.transpose()
        assertEquals(2, at.rows)
        assertEquals(2, at.cols)
        // at should be [[1,3],[2,4]]
        assertEquals(1.0, at[0, 0], EPS)
        assertEquals(3.0, at[0, 1], EPS)
        assertEquals(2.0, at[1, 0], EPS)
        assertEquals(4.0, at[1, 1], EPS)

        val b = DenseMatrix(2, 2, doubleArrayOf(
            0.5, 0.0,
            0.0, 2.0
        ))
        val ab = a.mul(b)
        // ab = [[0.5,4],[1.5,8]]
        assertEquals(0.5, ab[0, 0], EPS)
        assertEquals(4.0, ab[0, 1], EPS)
        assertEquals(1.5, ab[1, 0], EPS)
        assertEquals(8.0, ab[1, 1], EPS)
    }
}
