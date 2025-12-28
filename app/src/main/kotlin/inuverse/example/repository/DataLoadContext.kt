package inuverse.example.repository

class DataLoadContext<T>(
    var strategy: DataLoadStrategy<T>,
) {
    /**
     * 📝
     * Anyを使うと、型推論のせいかmnistImagesの方がAnyになってしまっていて、getメソッドを持たなくなってしまう
     * もともと、ロードしたデータは色々な形態を持つことからAnyにしていたが、このようなものはジェネリクスの方が効果的
     */
    fun load(path: String): T {
        return strategy.load(path)
    }
}