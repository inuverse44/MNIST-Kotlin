package inuverse.mnist.neural.layer

import inuverse.mnist.model.DenseVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SigmoidTest {

    private val eps = 1e-9

    @Test
    fun testForward() {
        val sigmoid = Sigmoid()
        
        // 0.0 -> 0.5
        val input = DenseVector(3, doubleArrayOf(0.0, 100.0, -100.0))
        val output = sigmoid.forward(input)
        
        assertEquals(0.5, output[0], eps)
        assertTrue(output[1] > 0.9999)
        assertTrue(output[2] < 0.0001)
    }

    @Test
    fun testBackward() {
        val sigmoid = Sigmoid()
        
        // y = sigmoid(0.0) = 0.5 となるようにForward
        val input = DenseVector(1, doubleArrayOf(0.0))
        sigmoid.forward(input)
        
        // 上流からの勾配 dL/dy = 1.0
        val outputGradient = DenseVector(1, doubleArrayOf(1.0))
        
        // Backward: dL/dx = dL/dy * y * (1-y) = 1.0 * 0.5 * 0.5 = 0.25
        val inputGradient = sigmoid.backward(outputGradient)
        
        assertEquals(0.25, inputGradient[0], eps)
    }

    @Test
    fun testBackwardValues() {
        val sigmoid = Sigmoid()
        
        // 適当な値でチェック
        val input = DenseVector(2, doubleArrayOf(1.0, -1.0))
        val output = sigmoid.forward(input)
        
        val outputGradient = DenseVector(2, doubleArrayOf(0.5, 0.5))
        val inputGradient = sigmoid.backward(outputGradient)
        
        for (i in 0 until input.size) {
            val y = output[i]
            val expected = outputGradient[i] * y * (1.0 - y)
            assertEquals(expected, inputGradient[i], eps)
        }
    }
}
