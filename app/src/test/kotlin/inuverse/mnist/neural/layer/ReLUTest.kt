package inuverse.mnist.neural.layer

import inuverse.mnist.model.DenseVector
import kotlin.test.Test
import kotlin.test.assertEquals

class ReLUTest {

    private val eps = 1e-9

    @Test
    fun testForward() {
        val relu = ReLU()
        val input = DenseVector(3, doubleArrayOf(1.0, -2.0, 0.0))
        val output = relu.forward(input)
        
        assertEquals(1.0, output[0], eps)
        assertEquals(0.0, output[1], eps)
        assertEquals(0.0, output[2], eps)
    }

    @Test
    fun testBackward() {
        val relu = ReLU()
        // Forwardして入力をキャッシュ
        val input = DenseVector(3, doubleArrayOf(2.0, -1.0, 0.0))
        relu.forward(input)
        
        // 上流からの勾配
        val outputGradient = DenseVector(3, doubleArrayOf(0.5, 0.5, 0.5))
        
        // Backward
        val inputGradient = relu.backward(outputGradient)
        
        // 入力が正(2.0) -> 勾配(0.5)をそのまま通す
        assertEquals(0.5, inputGradient[0], eps)
        // 入力が負(-1.0) -> 勾配を遮断して0.0
        assertEquals(0.0, inputGradient[1], eps)
        // 入力が0.0 -> 実装上は遮断して0.0
        assertEquals(0.0, inputGradient[2], eps)
    }
}
