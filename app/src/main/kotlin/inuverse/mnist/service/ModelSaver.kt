package inuverse.mnist.service

import inuverse.mnist.neural.Network
import java.io.File

class ModelSaver {

    fun save(network: Network, filepath: String) {
        val parameters = network.getModelParameters()
        val json = toJson(parameters)
        File(filepath).writeText(json)
        println("💾 Model saved to $filepath")
    }

    // 簡易的なJSONシリアライザ
    private fun toJson(data: Any): String {
        return when (data) {
            is Map<*, *> -> {
                val entries = data.entries.joinToString(",\n") { (k, v) ->
                    "\"$k\": ${toJson(v!!)}"
                }
                "{\n$entries\n}"
            }
            is List<*> -> {
                val items = data.joinToString(",\n") { toJson(it!!) }
                "[\n$items\n]"
            }
            is DoubleArray -> {
                // 配列が長すぎるので、適度に改行を入れたいところだが、単純に並べる
                "[" + data.joinToString(", ") + "]"
            }
            is String -> "\"$data\""
            is Number -> data.toString()
            else -> "\"$data\""
        }
    }
}
