package inuverse.mnist.neural

import inuverse.mnist.model.Vector
import inuverse.mnist.neural.layer.Layer
import inuverse.mnist.neural.loss.Loss
import inuverse.mnist.neural.optimizer.Optimizer

class Network(
    val loss: Loss,
    val optimizer: Optimizer
) {
    // 好きなように層を構成できるぜ
    private val layers = mutableListOf<Layer>()

    fun add(layer: Layer) {
        layers.add(layer)
    }

    /**
     * 推論（順伝播）
     */
    fun predict(input: Vector): Vector {
        var x = input
        for (layer in layers) {
            x = layer.forward(x)
        }
        return x
    }

    /**
     * 学習する
     * 順伝播 -> 誤差 -> 逆伝播 -> パラメタ更新
     * 返り値はloss
     */
    fun train(input: Vector, target: Vector): Double {
        // forward
        val prediction = predict(input)

        // lossの計算
        val error = loss.forward(prediction, target)

        // backward: loss関数から最初の勾配 dLoss/dy をもらう
        var gradient = loss.backward(prediction, target)

        // 各層を逆順に辿って勾配を伝播させる
        for (layer in layers.reversed()) {
            gradient = layer.backward(gradient)
        }

        // パラメタを更新
        for (layer in layers) {
            optimizer.update(layer)
        }

        return error
    }
}