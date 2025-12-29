package inuverse.mnist.model

import inuverse.mnist.neural.layer.Dense
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
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
        assertEquals(1.0, m[0, 0])
        assertEquals(2.0, m[0, 1])
        assertEquals(3.0, m[1, 0])
        assertEquals(4.0, m[1, 1])
        assertEquals(5.0, m[2, 0])
        assertEquals(6.0, m[2, 1])
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
        assertEquals(5.0, mv[0])
        assertEquals(11.0, mv[1])
        assertEquals(17.0, mv[2])
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
        assertEquals(5.0, result[0, 0])
        assertEquals(5.0, result[0, 1])
        assertEquals(5.0, result[1, 0])
        assertEquals(5.0, result[1, 1])
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
        assertEquals(-3.0, result[0, 0])
        assertEquals(-1.0, result[0, 1])
        assertEquals(1.0, result[1, 0])
        assertEquals(3.0, result[1, 1])
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


}