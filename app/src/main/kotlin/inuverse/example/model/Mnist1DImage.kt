package inuverse.example.model

data class Mnist1DImage(
    val index: Int,         // MNISTのラベルとの対応
    val image: IntArray    // 1次元画像 (0-255)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mnist1DImage

        if (index != other.index) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + image.contentHashCode()
        return result
    }
}