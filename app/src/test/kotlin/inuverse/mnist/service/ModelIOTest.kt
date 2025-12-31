package inuverse.mnist.service

import inuverse.mnist.model.DenseVector
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class ModelIOTest {
    @Test
    fun save_and_load_roundtrip_predictions_match() {
        // Build a tiny network
        val net = Network(loss = CrossEntropy(), optimizer = StochasticGradientDescent(0.01))
        net.add(Dense(4, 5))
        net.add(ReLU())
        net.add(Dense(5, 3))
        net.add(Softmax())

        // Predict on a fixed input
        val input = DenseVector(4, doubleArrayOf(0.1, -0.2, 0.3, 0.0))
        val y1 = net.predict(input).getData()

        // Save to temp file
        val tmp = Files.createTempFile("model-io-test", ".json").toFile()
        ModelSaver().save(net, tmp.absolutePath)

        // Load as a new network from JSON spec
        val loaded = ModelLoader().loadToNewNetwork(tmp.absolutePath, learningRate = 0.01)
        val y2 = loaded.predict(input).getData()

        // Compare predictions element-wise within tolerance
        assertEquals(y1.size, y2.size)
        for (i in y1.indices) {
            assertEquals(y1[i], y2[i], 1e-9)
        }
    }
}

