package inuverse.mnist.neural.loss

import inuverse.mnist.model.Vector
import inuverse.mnist.model.DenseVector
import kotlin.math.log

class CrossEntropy : Loss {

    private val eps = 1e-9

    /**
     * 交差エントロピー誤差
     * E = - \sum_k t_k * ln(y_k)
     * predicted: y (モデルの出力確率)
     * actual: t (正解ラベルのOne-hotベクトル)
     */
    override fun forward(predicted: Vector, actual: Vector): Double {
        require(predicted.size == actual.size) {
            "Dimension mismatch: predicted.size: ${predicted.size}, actual.size: ${actual.size}"
        }

        var sum = 0.0
        for (i in 0 until predicted.size) {
            // actual[i]が1の項のみが実質的に計算される (One-hotの場合)
            sum += actual[i] * log(predicted[i] + eps, Math.E)
        }
        return -sum
    }

    /**
     * 交差エントロピーの微分
     * dE/dy_k = - t_k / y_k
     */
    override fun backward(predicted: Vector, actual: Vector): Vector {
        require(predicted.size == actual.size) {
            "Dimension mismatch: predicted.size: ${predicted.size}, actual.size: ${actual.size}"
        }

        val result = DoubleArray(predicted.size) { i ->
            -actual[i] / (predicted[i] + eps)
        }
        return DenseVector(predicted.size, result)
    }
}
