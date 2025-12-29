package inuverse.mnist.repository
interface DataLoadStrategy<T> {
    val name: String
    fun load(path: String): T
}

