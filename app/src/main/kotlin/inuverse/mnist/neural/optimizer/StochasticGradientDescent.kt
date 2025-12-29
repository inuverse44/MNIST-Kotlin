package inuverse.mnist.neural.optimizer

import inuverse.mnist.neural.layer.Layer
import inuverse.mnist.neural.layer.Dense

class StochasticGradientDescent(
    val learningRate: Double
): Optimizer {
    override fun update(layer: Layer) {
        // Dense（全結合層）であるかチェック
        if (layer is Dense) {
            // W = W - eta * dL/dW
            layer.weights = layer.weights.subtract(
                layer.weightsGradient.scale(learningRate)
            )

            // b = b - eta * dL/db
            layer.bias = layer.bias.subtract(
                layer.biasGradient.scale(learningRate)
            )
        }

        // 他の層なとき
    }
}