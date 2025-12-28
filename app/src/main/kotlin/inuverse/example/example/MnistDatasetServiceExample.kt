package inuverse.example.example

import inuverse.example.repository.MnistImageLoadStrategyImpl
import inuverse.example.service.MnistDatasetService

class MnistDatasetServiceExample {
    fun run() {
        // MNISTの画像とラベルをロードする
        val mnistFilePath = "t10k-images.idx3-ubyte"
        val labelFilePath = "t10k-labels.idx1-ubyte"
        val imageContext = inuverse.example.repository.DataLoadContext(
            MnistImageLoadStrategyImpl()
        )
        val mnistImages = imageContext.load(mnistFilePath)
        val labelContext = inuverse.example.repository.DataLoadContext(
            inuverse.example.repository.MnistLabelLoadStrategyImpl()
        )
        val mnistLabels = labelContext.load(labelFilePath)

        // サービスを実行してみるか
        val mnistDatasetService = MnistDatasetService(
            mnistImages,
            mnistLabels
        )

        // 全データを取得
        val allData = mnistDatasetService.getAllDataset()
        println("Dataset size: ${allData.size}")

        // 分割して取得
        val (trainData, testData) = mnistDatasetService.getSplitDatasets(0.8)
        println("Train size: ${trainData.size}, Test size: ${testData.size}")

        // 中身を表示
        println("First input vector size: ${trainData[0].input.size}")
        println("First label vector: ${trainData[0].label}")

    }
}