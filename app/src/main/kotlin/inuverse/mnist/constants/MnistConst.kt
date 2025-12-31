package inuverse.mnist.constants

object MnistConst {
    const val ROW_LENGTH = 28
    const val COL_LENGTH = 28
    // Preferred constant for MNIST input vector length
    const val MNIST_INPUT_SIZE = ROW_LENGTH * COL_LENGTH
    // Backward-compat (will be removed): keep old names mapping to new values
    @Deprecated("Use MNIST_INPUT_SIZE")
    const val Mnist1DLength = MNIST_INPUT_SIZE

    /**
     * 📝
     * `hexdump -C -n 16 t10k-images.idx3-ubyte`を実行すると
     * 00000000  00 00 08 03 00 00 27 10  00 00 00 1c 00 00 00 1c  |......'.........|                                                                                                                                    │
     * │ 00000010
     * となる。
     * 先頭の`00 00 08 03`が下記のMAGIC_NUMBERに対応する
     * 最初の2ブロックは必ず00
     * 次の3ブロック目は
     *      08: unsigned byte (0-255 の整数) ← 今回はこれ
     *      09: signed byte
     *      0B: short (2バイト整数)
     *      0C: int (4バイト整数)
     * を表す。
     * 4ブロック目はデータの次元数を表し、03であるので3次元（枚数、行数、列数）に対応する
     * 0x00000803を10進数に変換すると2051となる
     */
    const val MAGIC_NUMBER = 2051
}
