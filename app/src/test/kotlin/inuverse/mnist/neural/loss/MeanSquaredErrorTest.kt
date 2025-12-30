package inuverse.mnist.neural.loss

import inuverse.mnist.model.DenseVector
import kotlin.test.Test
import kotlin.test.assertEquals

class MeanSquaredErrorTest {

    private val eps = 1e-9

    @Test
    fun testForward() {
        val mse = MeanSquaredError()
        val predicted = DenseVector(2, doubleArrayOf(1.0, 2.0))
        val actual = DenseVector(2, doubleArrayOf(0.0, 0.0))
        
        // E = 1/(2*2) * ((1-0)^2 + (2-0)^2) = 1/4 * (1 + 4) = 5/4 = 1.25
        val loss = mse.forward(predicted, actual)
        assertEquals(1.25, loss, eps)
    }

    @Test
    fun testBackward() {
        val mse = MeanSquaredError()
        val predicted = DenseVector(2, doubleArrayOf(1.0, 3.0))
        val actual = DenseVector(2, doubleArrayOf(0.0, 1.0))
        
        // grad = 1/N * (predicted - actual)
        // grad = 1/2 * ([1.0-0.0, 3.0-1.0]) = 1/2 * [1.0, 2.0] = [0.5, 1.0]
        val gradient = mse.backward(predicted, actual)
        
        assertEquals(0.5, gradient[0], eps)
        assertEquals(1.0, gradient[1], eps)
    }

    @Test
    fun testZeroLoss() {
        val mse = MeanSquaredError()
        val predicted = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))
        val actual = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))
        
        assertEquals(0.0, mse.forward(predicted, actual), eps)
        val gradient = mse.backward(predicted, actual)
        assertEquals(0.0, gradient[0], eps)
        assertEquals(0.0, gradient[1], eps)
        assertEquals(0.0, gradient[2], eps)
    }
}
