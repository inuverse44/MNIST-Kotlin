package inuverse.example.repository

import inuverse.example.constants.MnistConst
import inuverse.example.model.Mnist1DImage
import inuverse.example.model.MnistLabel
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class MnistLabelLoadStrategyImpl: DataLoadStrategy<Array<MnistLabel>> {
    override val name = "MNIST Label is loading..."

    override fun load(path: String): Array<MnistLabel>  {
        try {
            val file = java.io.File(path)
            if (!file.exists()) throw Exception("File not found: $path")

            java.io.DataInputStream(java.io.FileInputStream(file)).use { stream ->
                val magicNumber = stream.readInt()
                val numberOfItems = stream.readInt()

                println("Label Magic Number: $magicNumber")
                println("Number of Labels: $numberOfItems")

                if (magicNumber != 2049) {
                    throw Exception("This is not a valid MNIST label file.")
                }

                // ラベルデータを一括読み込み
                val labelBuffer = ByteArray(numberOfItems)
                stream.readFully(labelBuffer)

                // MnistLabelの配列に変換
                return Array(numberOfItems) { i ->
                    MnistLabel(i, labelBuffer[i].toInt())
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }
}