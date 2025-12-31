package inuverse.mnist.neural.layer

import inuverse.mnist.model.DenseVector
import kotlin.test.Test
import kotlin.test.assertEquals

class DenseBackwardTest {
    @Test
    fun backward_shapes_match() {
        val layer = Dense(inputSize = 3, outputSize = 2)
        val x = DenseVector(3, doubleArrayOf(1.0, -2.0, 0.5))
        val _ = layer.forward(x)
        // upstream gradient same shape as y (2)
        val gy = DenseVector(2, doubleArrayOf(0.1, -0.2))
        val gx = layer.backward(gy)
        // input gradient must match input size
        assertEquals(3, gx.size)
        // gradients for params should match layer dims
        assertEquals(2, layer.biasGradient.size)
        assertEquals(2, layer.weightsGradient.rows)
        assertEquals(3, layer.weightsGradient.cols)
    }
}

