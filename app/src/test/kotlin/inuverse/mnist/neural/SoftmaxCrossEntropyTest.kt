package inuverse.mnist.neural

import inuverse.mnist.EPS
import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SoftmaxCrossEntropyTest {
    @Test
    fun softmax_basic_properties() {
        val layer = Softmax()
        val input = DenseVector(3, doubleArrayOf(1.0, 2.0, 3.0))
        val out = layer.forward(input)
        var sum = 0.0
        for (i in 0 until out.size) sum += out[i]
        assertEquals(1.0, sum, EPS)
        for (i in 0 until out.size) assertTrue(out[i] >= 0.0)
    }

    @Test
    fun cross_entropy_gradient_shape() {
        val loss = CrossEntropy()
        val y = DenseVector(3, doubleArrayOf(0.2, 0.3, 0.5))
        val t = DenseVector(3, doubleArrayOf(0.0, 0.0, 1.0))
        // compute loss to initialize any internal state (not strictly necessary)
        loss.forward(y, t)
        val grad = loss.backward(y, t)
        assertEquals(3, grad.size)
        // grad[2] should be negative and magnitude ~ 1/y[2]
        assertTrue(grad[2] < 0.0)
    }
}
