package inuverse.mnist.presentation

import inuverse.mnist.constants.MnistConst
import inuverse.mnist.model.Mnist1DImage

class MnistAsciiArt {
    fun draw(mnist1DImage: Mnist1DImage) {
        val row = MnistConst.ROW_LENGTH
        val col = MnistConst.COL_LENGTH
        for(r in 0 until row) {
            for(c in 0 until col) {
                val index = r * MnistConst.COL_LENGTH + c
                val pixelValue = mnist1DImage.image[index] // 0~255のInt
                val marker = if (pixelValue > 128) "##" else if (pixelValue > 0) ".." else "  "
                print(marker)
            }
            println()
        }
    }
}
