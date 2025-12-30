package inuverse.mnist.neural.layer

import inuverse.mnist.model.Vector
import inuverse.mnist.model.Matrix
import inuverse.mnist.model.DenseVector
import inuverse.mnist.model.DenseMatrix
import kotlin.random.Random

class Dense(
    val inputSize: Int,
    val outputSize: Int,
    random: Random = Random.Default
): Layer {

    /**
     * 📝
     * 初期化するときにはランダムでハイパーパラメタ（重みとバイアスに値を与える）
     * \vec{xNext} = W \vec{x} + \vec{b}
     * まずは重みから。-1.0 ~ 1.0
     */
    var weights: Matrix = DenseMatrix(
        outputSize,
        inputSize,
        DoubleArray(outputSize * inputSize) { random.nextDouble() * 2 - 1 }
        )

    var bias: Vector = DenseVector(outputSize, DoubleArray(outputSize))

    // キャッシュ
    private lateinit var input: Vector

    // 勾配（Optimizer用）
    lateinit var weightsGradient: Matrix
    lateinit var biasGradient: Vector

    override fun forward(input: Vector): Vector {
        this.input = input
        // y = Wx + b
        return weights.apply(input).add(bias)
    }

    override fun backward(outputGradient: Vector): Vector {
        // 1. 重みの勾配 dE/dW = dy/dW * dE/dy = outputGradient * input^T (Outer product)
        // outputGradient: OutputDim
        // input: InputDim
        // weightGradient: OutputDim x InputDim
        this.weightsGradient = outputGradient.outerProduct(input)

        // 2. バイアスの勾配 dE/db = outputGradient
        this.biasGradient = outputGradient
        
        // 3. 入力への勾配 dE/dx = dE/dy * W (転置)
        // W: Output x Input
        // W^T: Input x Output
        // outputGradient: Output
        // result: Input
        val inputGradient = weights.transpose().apply(outputGradient)
        
        return inputGradient
    }
    
    // Optimizerからアクセスするためのプロパティ
    val w: Matrix get() = weights
    val b: Vector get() = bias
    
    override fun getParameters(): Map<String, Any> {
        return mapOf(
            "rows" to weights.rows,
            "cols" to weights.cols,
            "weights" to weights.getData(),
            "biases" to bias.getData()
        )
    }

    override fun loadParameters(params: Map<String, Any>) {
        val rows = (params["rows"] as Number).toInt()
        val cols = (params["cols"] as Number).toInt()
        
        // JSONからの復元時は List<Double> または DoubleArray になる可能性があるためキャスト注意
        val weightData = (params["weights"] as List<Double>).toDoubleArray()
        val biasData = (params["biases"] as List<Double>).toDoubleArray()
        
        // サイズチェックは省略するが、本来はすべき
        this.weights = DenseMatrix(rows, cols, weightData)
        this.bias = DenseVector(rows, biasData)
        
        // Gradientのサイズも変わる可能性があるため再初期化が必要だが、
        // 推論のみに使う場合はGradientは不要。学習再開する場合は注意。
    }
}
