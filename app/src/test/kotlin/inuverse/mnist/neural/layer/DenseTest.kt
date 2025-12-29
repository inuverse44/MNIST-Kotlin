package inuverse.mnist.neural.layer

import inuverse.mnist.model.DenseVector
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DenseTest {

    private val eps = 1e-9

    @Test
    fun testInitialization() {
        val inputSize = 3
        val outputSize = 2
        val random = Random(12345)
        
        val dense = Dense(inputSize, outputSize, random)
        
        assertEquals(inputSize, dense.inputSize)
        assertEquals(outputSize, dense.outputSize)
        assertEquals(2, dense.weights.rows)
        assertEquals(3, dense.weights.cols)
        assertEquals(2, dense.bias.size)
    }

    @Test
    fun testForwardWithFixedSeed() {
        val random = Random(1) // Seed=1
        val dense = Dense(2, 2, random)

        val input = DenseVector(2, doubleArrayOf(1.0, 2.0))

        val output = dense.forward(input)

        assertEquals(2, output.size)

        val random2 = Random(1)
        val dense2 = Dense(2, 2, random2)
        val output2 = dense2.forward(input)
        
        assertEquals(output[0], output2[0], eps)
        assertEquals(output[1], output2[1], eps)
    }

    @Test
    fun testBackward() {
        val random = Random(42)
        val dense = Dense(2, 2, random)
        val input = DenseVector(2, doubleArrayOf(1.0, 2.0))

        dense.forward(input)

        val outputGradient = DenseVector(2, doubleArrayOf(0.5, -0.5))
        val inputGradient = dense.backward(outputGradient)

        // 入力への勾配のサイズ (inputSizeと同じはず)
        assertEquals(2, inputGradient.size)
        
        // 内部パラメータの勾配が計算されているか
        assertNotNull(dense.weightsGradient)
        assertNotNull(dense.biasGradient)
        
        // 勾配のサイズチェック
        assertEquals(2, dense.weightsGradient.rows)
        assertEquals(2, dense.weightsGradient.cols)
        assertEquals(2, dense.biasGradient.size)
        
        // バイアスの勾配はoutputGradientそのものになるはず (dL/db = dL/dy)
        assertEquals(0.5, dense.biasGradient[0], eps)
        assertEquals(-0.5, dense.biasGradient[1], eps)
    }
}

