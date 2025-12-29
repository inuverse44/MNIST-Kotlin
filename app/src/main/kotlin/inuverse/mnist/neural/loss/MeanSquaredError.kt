package inuverse.mnist.neural.loss

import inuverse.mnist.model.Vector

class MeanSquaredError: Loss {
    /**
     * 2乗平均誤差
     * E = \frac{1}{2N} \sum_k (y_k - t_k)^2
     */
    override fun forward(predicted: Vector, actual: Vector): Double {
        require(predicted.size == actual.size) {
            "Dimension mismatch: predicted.size: ${predicted.size}, actual.size: ${actual.size}"
        }
        val result: Double
        var sum = 0.0
        for (i in 0 until predicted.size) {
            val diff = predicted[i] - actual[i]
            sum += diff * diff
        }
        result = sum / (2.0 * predicted.size)
        return result
    }

    /**
     * 解析的には
     * d Loss/d y_k = \frac{1}{N} (y_k - t_k)
     * と計算できますね
     */
    override fun backward(predicted: Vector, actual: Vector): Vector {
        require(predicted.size == actual.size) {
            "Dimension mismatch: predicted.size: ${predicted.size}, actual.size: ${actual.size}"
        }
        val diff = predicted.subtract(actual)
        return diff.scale(1.0 / predicted.size)

    }
}