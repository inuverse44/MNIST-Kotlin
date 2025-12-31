package inuverse.mnist.service

import inuverse.mnist.model.Vector
import inuverse.mnist.neural.Network
import inuverse.mnist.service.MnistDatasetService.DataPair
import org.slf4j.LoggerFactory

class MnistTrainer(
    private val network: Network,
    private val trainData: List<DataPair>,
    private val testData: List<DataPair>
) {
    private val logger = LoggerFactory.getLogger(MnistTrainer::class.java)

    data class TrainingHistory(
        val epoch: Int,
        val loss: Double,
        val accuracy: Double
    )

    /**
     * 学習を実行する
     * @param epochs エポック数
     * @param reportInterval ログ出力の間隔（エポック単位）
     * @return 学習履歴のリスト
     */
    fun train(epochs: Int, reportInterval: Int = 1): List<TrainingHistory> {
        logger.info("Start Training for $epochs epochs...")
        logger.info("Train Data: ${trainData.size}, Test Data: ${testData.size}")

        val history = mutableListOf<TrainingHistory>()

        for (epoch in 1..epochs) {
            val startTime = System.currentTimeMillis()
            var totalLoss = 0.0

            // データをシャッフル
            val shuffledData = trainData.shuffled()

            // 1データずつ学習 (SGD)
            for (data in shuffledData) {
                totalLoss += network.train(data.input, data.label)
            }

            val avgLoss = totalLoss / trainData.size
            val duration = System.currentTimeMillis() - startTime

            // 精度計測（毎回行うと重いので、グラフ用には毎回計測するか、補間するか考えるが、今回は毎回計測する）
            val accuracy = evaluate(testData)
            
            history.add(TrainingHistory(epoch, avgLoss, accuracy))

            if (epoch % reportInterval == 0 || epoch == 1 || epoch == epochs) {
                val accuracyPercent = "%.2f".format(accuracy * 100)
                logger.info("Epoch $epoch/$epochs | Loss: %.6f | Accuracy: $accuracyPercent%% | Time: ${duration}ms".format(avgLoss))
            }
        }
        logger.info("Training Finished!")
        return history
    }

    /**
     * 学習（エポックごとにコールバックを実行）
     */
    fun trainWithCallback(
        epochs: Int,
        reportInterval: Int = 1,
        onEpoch: (epoch: Int, loss: Double, accuracy: Double) -> Unit
    ): List<TrainingHistory> {
        val history = mutableListOf<TrainingHistory>()

        for (epoch in 1..epochs) {
            val startTime = System.currentTimeMillis()
            var totalLoss = 0.0

            val shuffledData = trainData.shuffled()
            for (data in shuffledData) {
                totalLoss += network.train(data.input, data.label)
            }

            val avgLoss = totalLoss / trainData.size
            val duration = System.currentTimeMillis() - startTime
            val accuracy = evaluate(testData)

            val th = TrainingHistory(epoch, avgLoss, accuracy)
            history.add(th)

            if (epoch % reportInterval == 0 || epoch == 1 || epoch == epochs) {
                val accuracyPercent = "%.2f".format(accuracy * 100)
                logger.info("Epoch $epoch/$epochs | Loss: %.6f | Accuracy: $accuracyPercent%% | Time: ${duration}ms".format(avgLoss))
            }

            onEpoch(epoch, avgLoss, accuracy)
        }
        logger.info("Training Finished!")
        return history
    }

    /**
     * 評価を実行し、正解率 (0.0 - 1.0) を返す
     */
    fun evaluate(dataset: List<DataPair>): Double {
        var correctCount = 0
        
        for (data in dataset) {
            val prediction = network.predict(data.input)
            val predictedLabel = argmax(prediction)
            val actualLabel = argmax(data.label)
            
            if (predictedLabel == actualLabel) {
                correctCount++
            }
        }
        
        return correctCount.toDouble() / dataset.size
    }

    /**
     * ベクトルの中で最も値が大きいインデックスを返す
     */
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
