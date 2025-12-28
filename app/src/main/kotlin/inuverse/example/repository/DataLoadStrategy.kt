package inuverse.example.repository
interface DataLoadStrategy<T> {
    val name: String
    fun load(path: String): T
}

