package inuverse.mnist.neural

import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent
import kotlin.test.Test
import kotlin.test.assertEquals

class NetworkSmokeTest {
    @Test
    fun simple_train_step_runs() {
        val net = Network(loss = CrossEntropy(), optimizer = StochasticGradientDescent(0.01))
        net.add(Dense(4, 5))
        net.add(ReLU())
        net.add(Dense(5, 3))
        net.add(Softmax())

        val x = DenseVector(4, doubleArrayOf(0.3, -0.1, 0.5, 0.0))
        val t = DenseVector(3, doubleArrayOf(0.0, 1.0, 0.0))
        val loss = net.train(x, t)
        // Loss should be finite and positive
        assertEquals(true, loss.isFinite() && loss > 0.0)
    }
}

