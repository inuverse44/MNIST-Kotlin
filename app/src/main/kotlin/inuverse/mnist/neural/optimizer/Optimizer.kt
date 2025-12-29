package inuverse.mnist.neural.optimizer

import inuverse.mnist.neural.layer.Layer

interface Optimizer {
    /**
     * 更新対象の層のパラメタを更新する
     */
    fun update(layer: Layer)
}