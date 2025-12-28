package inuverse.example.example

import inuverse.example.presentation.MnistAsciiArt
import inuverse.example.repository.MnistImageLoadStrategyImpl

class MnistAsciiExample {
    fun exampleMnistAscii() {
        val mnistFilePath = "t10k-images.idx3-ubyte"
        val labelFilePath = "t10k-labels.idx1-ubyte"

        // 画像用コンテキスト
        val imageContext = inuverse.example.repository.DataLoadContext(
            MnistImageLoadStrategyImpl()
        )
        val mnistImages = imageContext.load(mnistFilePath)

        // ラベル用コンテキスト（型が違うのでインスタンスを分ける）
        // もしかして、ストラテジパターンを導入した意味ないかもーー！！
        val labelContext = inuverse.example.repository.DataLoadContext(
            inuverse.example.repository.MnistLabelLoadStrategyImpl()
        )
        val mnistLabels = labelContext.load(labelFilePath)


        println("\n----- First 5 Images & Labels -----\n")
        for (i in 0 until 5) {
            println("Index: $i")
            println("Label: ${mnistLabels[i].value}") // 正解ラベルを表示
            MnistAsciiArt().draw(mnistImages[i])
            println("--------------------------------------------------")
        }
    }
}