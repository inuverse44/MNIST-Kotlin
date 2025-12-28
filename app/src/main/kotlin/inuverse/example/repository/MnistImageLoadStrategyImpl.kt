package inuverse.example.repository

import inuverse.example.constants.MnistConst
import inuverse.example.model.Mnist1DImage
import java.io.File
import java.io.DataInputStream
import java.io.FileInputStream

class MnistImageLoadStrategyImpl: DataLoadStrategy<Array<Mnist1DImage>> {
    override val name = "MNIST image is loading..."

    override fun load(path: String): Array<Mnist1DImage> {
        try {
            val file = File(path)

            if (!file.exists()) {
                throw Exception("File not found: $path")
            }

            DataInputStream(FileInputStream(file)).use { stream ->
                // readInt()は読み込んだファイルを4バイトずつ読み込んで、ポインタを4つ進める動作をする
                val magicNumber = stream.readInt()
                val numberOfImages = stream.readInt()
                val rows = stream.readInt()
                val cols = stream.readInt()

                // hexdump -C -n 16 t10k-images.idx3-ubyte
                if (magicNumber != MnistConst.MAGIC_NUMBER) {
                    throw Exception("This is not a valid MNIST image file.")
                }

                /**
                 * 📝
                 * Arrayの初期化ブロックで、サイズと中身を渡す書き方を覚えような、おれ
                 */
                val imageBuffer = ByteArray(rows * cols)
                val images = Array(numberOfImages) { i ->
                    stream.readFully(imageBuffer)
                    // Byte(-128~127) を Int(0~255) に変換する
                    val intImage = IntArray(rows * cols) { idx ->
                        imageBuffer[idx].toUByte().toInt()
                    }
                    Mnist1DImage(i, intImage)
                }

                return images
            }
        } catch (e: Exception) {
            throw e
        }

    }
}