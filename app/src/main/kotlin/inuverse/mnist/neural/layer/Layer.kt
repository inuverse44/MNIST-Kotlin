package inuverse.mnist.neural.layer

import inuverse.mnist.model.Vector

interface Layer {
    fun forward(input: Vector): Vector
    fun backward(outputGradient: Vector): Vector
    
    // パラメータ保存用
    fun getParameters(): Map<String, Any> = emptyMap()
    // パラメータ読み込み用
    fun loadParameters(params: Map<String, Any>) {}
    fun getName(): String = this::class.simpleName ?: "Layer"
}