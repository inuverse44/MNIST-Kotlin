package inuverse.mnist.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DenseMatrixTest {

    private val eps = 1e-9

    @Test
    fun testMatrixRowsAndCols() {
        val matrixData = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val m = DenseMatrix(3, 2, matrixData)
        assertEquals(3, m.rows)
        assertEquals(2, m.cols)
    }

    @Test
    fun testMatrixGet() {
        val matrixData = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val m = DenseMatrix(3, 2, matrixData)
        assertEquals(1.0, m[0, 0], eps)
        assertEquals(2.0, m[0, 1], eps)
        assertEquals(3.0, m[1, 0], eps)
        assertEquals(4.0, m[1, 1], eps)
        assertEquals(5.0, m[2, 0], eps)
        assertEquals(6.0, m[2, 1], eps)
    }

    @Test
    fun testMatrixApply() {
        val matrixData = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val m = DenseMatrix(3, 2, matrixData)
        val vectorData = doubleArrayOf(
            1.0,
            2.0
        )
        val v = DenseVector(2, vectorData)
        val mv = m.apply(v)
        assertEquals(5.0, mv[0], eps)
        assertEquals(11.0, mv[1], eps)
        assertEquals(17.0, mv[2], eps)
    }

    @Test
    fun testMatrixApplyMismatch() {
        val matrixData = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val m = DenseMatrix(3, 2, matrixData)
        val vectorData = doubleArrayOf(
            1.0,
            2.0,
            3.0
        )
        val v = DenseVector(3, vectorData)
        assertFailsWith<IllegalArgumentException> {
            m.apply(v)
        }
    }

    @Test
    fun testMatrixAdd() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val matrixDataB = doubleArrayOf(
            4.0, 3.0,
            2.0, 1.0
        )
        val mB = DenseMatrix(2, 2, matrixDataB)
        val result = mA.add(mB)
        assertEquals(5.0, result[0, 0], eps)
        assertEquals(5.0, result[0, 1], eps)
        assertEquals(5.0, result[1, 0], eps)
        assertEquals(5.0, result[1, 1], eps)
    }

    @Test
    fun testMatrixAddMismatch() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val matrixDataB = doubleArrayOf(
            4.0, 3.0,
            2.0, 1.0,
            5.0, 6.0
        )
        val mB = DenseMatrix(3, 2, matrixDataB)
        assertFailsWith<IllegalArgumentException> {
            mA.add(mB)
        }
    }

    @Test
    fun testMatrixSubtract() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val matrixDataB = doubleArrayOf(
            4.0, 3.0,
            2.0, 1.0
        )
        val mB = DenseMatrix(2, 2, matrixDataB)
        val result = mA.subtract(mB)
        assertEquals(-3.0, result[0, 0], eps)
        assertEquals(-1.0, result[0, 1], eps)
        assertEquals(1.0, result[1, 0], eps)
        assertEquals(3.0, result[1, 1], eps)
    }

    @Test
    fun testMatrixSubtractMismatch() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val matrixDataB = doubleArrayOf(
            4.0, 3.0,
            2.0, 1.0,
            5.0, 6.0
        )
        val mB = DenseMatrix(3, 2, matrixDataB)
        assertFailsWith<IllegalArgumentException> {
            mA.subtract(mB)
        }
    }

    @Test
    fun testMatrixMul() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val matrixDataB = doubleArrayOf(
            4.0, 3.0,
            2.0, 1.0
        )
        val mB = DenseMatrix(2, 2, matrixDataB)
        val result = mA.mul(mB)
        assertEquals(8.0, result[0, 0], eps)
        assertEquals(5.0, result[0, 1], eps)
        assertEquals(20.0, result[1, 0], eps)
        assertEquals(13.0, result[1, 1], eps)
    }

    @Test
    fun testMatrixMulMismatch() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val matrixDataB = doubleArrayOf(
            4.0, 3.0,
            2.0, 1.0,
            5.0, 6.0
        )
        val mB = DenseMatrix(3, 2, matrixDataB)
        assertFailsWith<IllegalArgumentException> {
            mA.mul(mB)
        }
    }

    @Test
    fun testMatrixScale() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0
        )
        val mA = DenseMatrix(2, 2, matrixDataA)
        val result = mA.scale(2.0)
        assertEquals(2.0, result[0, 0], eps)
        assertEquals(4.0, result[0, 1], eps)
        assertEquals(6.0, result[1, 0], eps)
        assertEquals(8.0, result[1, 1], eps)
    }

    @Test
    fun testMatrixTranspose() {
        val matrixDataA = doubleArrayOf(
            1.0, 2.0,
            3.0, 4.0,
            5.0, 6.0
        )
        val mA = DenseMatrix(3, 2, matrixDataA)
        val result = mA.transpose()
        assertEquals(1.0, result[0, 0], eps)
        assertEquals(3.0, result[0, 1], eps)
        assertEquals(5.0, result[0, 2], eps)
        assertEquals(2.0, result[1, 0], eps)
        assertEquals(4.0, result[1, 1], eps)
        assertEquals(6.0, result[1, 2], eps)
    }
}
