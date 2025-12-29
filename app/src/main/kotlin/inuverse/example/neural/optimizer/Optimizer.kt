package inuverse.example.neural.optimizer

import inuverse.example.neural.layer.Layer

interface Optimizer {
    /**
     * 更新対象の層のパラメタを更新する
     */
    fun update(layer: Layer)
}