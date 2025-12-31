package inuverse.mnist.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import inuverse.mnist.neural.Network
import java.io.File

class ModelLoader {
    private val mapper = jacksonObjectMapper()

    /**
     * ファイルからパラメータを読み込み、ネットワークに適用する
     * 注意: Networkの構造保存時と同じである必要がある
     * Networkの構造も保存しておきたいなあ
     */
    fun load(filepath: String, network: Network) {
        val file = File(filepath)
        if (!file.exists()) {
            throw IllegalArgumentException("Model file not found: $filepath")
        }

        println("🐶Loading model from $filepath ...")

        // JSONを List<Map<String, Any>> として読み込む
        // 構造: [ { "type": "Dense", "params": {...} }, ... ]
        val layersData: List<Map<String, Any>> = mapper.readValue(file)
        
        val networkLayers = network.getLayers() // NetworkにgetLayersを追加する必要がある

        if (layersData.size != networkLayers.size) {
            println("🐶Warning: Layer count mismatch! File: ${layersData.size}, Network: ${networkLayers.size}")
        }

        // 各レイヤーにパラメータをセット
        for ((index, layerData) in layersData.withIndex()) {
            if (index >= networkLayers.size) break
            
            val layerName = layerData["type"] as String
            val params = layerData["params"] as Map<String, Any>
            
            val targetLayer = networkLayers[index]
            
            // 型チェック（簡易）
            if (targetLayer.getName() != layerName) {
                println("🐶Warning: Layer type mismatch at index $index. File: $layerName, Network: ${targetLayer.getName()}")
            }

            // パラメータ読み込み
            if (params.isNotEmpty()) {
                targetLayer.loadParameters(params)
            }
        }
        println("🐶Model loaded successfully!")
    }
}
