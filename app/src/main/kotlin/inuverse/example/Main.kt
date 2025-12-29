package inuverse.example

import inuverse.example.repository.MnistImageLoadStrategyImpl
import inuverse.example.repository.MnistLabelLoadStrategyImpl
import inuverse.example.repository.DataLoadContext
import inuverse.example.service.MnistDatasetService
import inuverse.example.neural.Network
import inuverse.example.neural.layer.Dense
import inuverse.example.neural.layer.Sigmoid
import inuverse.example.neural.loss.MeanSquaredError
import inuverse.example.neural.optimizer.StochasticGradientDescent
import kotlin.random.Random

fun main() {
    println("🐶 Inuverse: MNIST Learning Demo Started! 🐶")

    // 1. Load Data
    println("Loading MNIST data...")
    val imageContext = DataLoadContext(MnistImageLoadStrategyImpl())
    val labelContext = DataLoadContext(MnistLabelLoadStrategyImpl())
    
    // ファイルパスはプロジェクトルートからの相対パスと仮定
    val mnistImages = imageContext.load("t10k-images.idx3-ubyte")
    val mnistLabels = labelContext.load("t10k-labels.idx1-ubyte")

    val service = MnistDatasetService(mnistImages, mnistLabels)
    
    // 全データを取得し、シャッフルして訓練データとする（今回はテスト分割は省略）
    val dataset = service.getAllDataset().shuffled(Random(123))
    val trainData = dataset.take(8000)
    println("Data loaded. Training with ${trainData.size} samples.")

    // 2. Build Network
    // Input(784) -> Hidden(100) -> Output(10)
    val inputSize = 784
    val hiddenSize = 100
    val outputSize = 10
    val learningRate = 0.01

    val network = Network(
        loss = MeanSquaredError(),
        optimizer = StochasticGradientDescent(learningRate)
    )

    network.add(Dense(inputSize, hiddenSize))
    network.add(Sigmoid())
    network.add(Dense(hiddenSize, outputSize))
    network.add(Sigmoid())

    println("Network built. Input: $inputSize, Hidden: $hiddenSize, Output: $outputSize")
    println("Learning Rate: $learningRate")

    // 3. Training Loop
    val epochs = 100
    println("Start training for $epochs epochs...")

    for (epoch in 1..epochs) {
        var totalLoss = 0.0
        val startTime = System.currentTimeMillis()

        // データをシャッフル（毎回順番を変えるのがSGDのコツ）
        val epochData = trainData.shuffled()

        for (data in epochData) {
            val loss = network.train(data.input, data.label)
            totalLoss += loss
        }

        val avgLoss = totalLoss / epochData.size
        val duration = System.currentTimeMillis() - startTime
        
        println("Epoch $epoch/$epochs | Loss: %.6f | Time: ${duration}ms".format(avgLoss))
        
        // 簡易的な精度チェック（最初の1件でテスト）
        if (epoch % 5 == 0) {
            val sample = epochData[0]
            val output = network.predict(sample.input)
            // 最大値のインデックスを探す（argmaxの実装がないので簡易的に）
            var maxIdx = 0
            var maxVal = output[0]
            for(i in 1 until output.size) {
                if(output[i] > maxVal) {
                    maxVal = output[i]
                    maxIdx = i
                }
            }
            // 正解ラベルのインデックス
            var labelIdx = 0
            for(i in 0 until sample.label.size) {
                if(sample.label[i] == 1.0) labelIdx = i
            }
            println("  Sample Check -> Prediction: $maxIdx, Actual: $labelIdx")
        }
    }
    
    println("Training finished! 🐾")
}