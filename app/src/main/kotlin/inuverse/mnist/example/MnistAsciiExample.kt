package inuverse.mnist.example

import inuverse.mnist.presentation.MnistAsciiArt
import inuverse.mnist.repository.MnistImageLoadStrategyImpl

class MnistAsciiExample {
    fun exampleMnistAscii() {
        val mnistFilePath = "t10k-images.idx3-ubyte"
        val labelFilePath = "t10k-labels.idx1-ubyte"

        // 画像用コンテキスト
        val imageContext = inuverse.mnist.repository.DataLoadContext(
            MnistImageLoadStrategyImpl()
        )
        val mnistImages = imageContext.load(mnistFilePath)

        // ラベル用コンテキスト（型が違うのでインスタンスを分ける）
        // もしかして、ストラテジパターンを試しに導入した意味ないかもーー！！
        val labelContext = inuverse.mnist.repository.DataLoadContext(
            inuverse.mnist.repository.MnistLabelLoadStrategyImpl()
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