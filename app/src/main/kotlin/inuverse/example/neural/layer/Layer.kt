package inuverse.example.neural.layer

import inuverse.example.model.Vector

interface Layer {
    /**
     * 入力ベクトル x
     * 出力ベクトル y
     * とすると↓
     */
    fun forward(input: Vector): Vector

    /**
     * 出力側から伝わってきた勾配 \nabla_y L
     * として、\nabla_x L を返す
     */
    fun backward(outputGradient: Vector): Vector
}