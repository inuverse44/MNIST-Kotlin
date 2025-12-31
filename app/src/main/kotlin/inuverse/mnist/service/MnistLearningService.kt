package inuverse.mnist.service

import inuverse.mnist.model.TrainingConfig
import inuverse.mnist.model.Vector
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.ReLU
import inuverse.mnist.neural.layer.Softmax
import inuverse.mnist.neural.loss.CrossEntropy
import inuverse.mnist.neural.optimizer.StochasticGradientDescent
import inuverse.mnist.presentation.LossPlotter
import kotlin.random.Random


/**
 * MNIST学習アプリケーションのユースケースを提供するサービス
 */
class MnistLearningService(
    private val datasetService: MnistDatasetService
) {

    /**
     * 設定に基づいて学習プロセス全体を実行する
     */
    fun runTraining(config: TrainingConfig) {
        println("🐶 Configuration: $config")

        // データ準備
        println("🐶 Preparing Dataset...")
        val allData = datasetService.getAllDataset().shuffled(Random(config.randomSeed))
        
        val trainData = allData.take(config.trainSize)
        val testData = allData.drop(config.trainSize).take(config.testSize)
        
        println("   Train: ${trainData.size}, Test: ${testData.size}")

        // ネットワークの構築
        println("🐶 Building Network...")
        val network = buildNetwork(config)

        // 学習
        val trainer = MnistTrainer(network, trainData, testData)
        val history = trainer.train(config.epochs)

        val prediction = getPrediction(testData[0].input, network)
        val actualLabel = testData[0].label
//        println("\n🐶Check: input: ${testData[0].input}")
//        println("\n🐶Check: prediction: $prediction")
//        println("\n🐶Check: label: $actualLabel")

        // 可視化
        println("\n🐶 Generating Training Graphs...")
        LossPlotter().plot(history)

        println("🐶Final Evaluation on Test Data...")
        val finalAccuracy = trainer.evaluate(testData)
        println("   Test Accuracy: %.2f%%\n".format(finalAccuracy * 100))
        
        // モデルの保存
        ModelSaver().save(network, "mnist_model.json")
    }

    /**
     * 784成分ベクトルを入れて、もっとも正解っぽいonehot表現のベクトルを返してくれるやつ
     * @param input 入力の784成分ベクトル
     * @param network 最適化後のネットワーク
     * @return Vector onehot表現の正解っぽいベクトル
     */
    fun getPrediction(input: Vector, network: Network): Vector {
        val prediction = network.predict(input)
        return prediction
    }

    private fun buildNetwork(config: TrainingConfig): Network {
        val network = Network(
            loss = CrossEntropy(),
            optimizer = StochasticGradientDescent(config.learningRate)
        )

        // Input -> Hidden -> Output
        network.add(Dense(inuverse.mnist.constants.MnistConst.MNIST_INPUT_SIZE, config.hiddenLayerSize))
        network.add(ReLU())
        network.add(Dense(config.hiddenLayerSize, 10))
        network.add(Softmax())

        return network
    }

    /**
     * 保存されたモデルをロードして推論のデモを行う
     */
    fun runInferenceDemo(config: TrainingConfig, modelPath: String) {
        println("\n🐶Starting Inference Demo using Saved Model...")

        // 1. JSON (ModelSpec) から動的にネットワークを構築
        val network = try {
            ModelLoader().loadToNewNetwork(modelPath, learningRate = config.learningRate)
        } catch (e: Exception) {
            println("   Failed to load model: ${e.message}")
            return
        }

        // 3. テストデータの一部を使って推論
        val allData = datasetService.getAllDataset().shuffled(Random(config.randomSeed))
        val testSamples = allData.drop(config.trainSize).take(5) // 5件だけピックアップ

        println("\n--- Inference Results ---")
        var correct = 0
        for ((index, sample) in testSamples.withIndex()) {
            val output = network.predict(sample.input)
            
            val predictedLabel = argmax(output)
            val actualLabel = argmax(sample.label)
            val probability = output[predictedLabel] * 100 // onehot表現の出力ベクトルには各成分に重みがある。

            val result = if (predictedLabel == actualLabel) "👍OK" else "👎"
            if (predictedLabel == actualLabel) correct++

            println("Sample #$index: Actual [$actualLabel] -> Predicted [$predictedLabel] (Prob: %.2f%%) $result".format(probability))
        }
        println("-------------------------")
    }

    private fun argmax(vector: Vector): Int {
        var maxIndex = 0
        var maxValue = vector[0]
        for (i in 1 until vector.size) {
            if (vector[i] > maxValue) {
                maxValue = vector[i]
                maxIndex = i
            }
        }
        return maxIndex
    }
}
