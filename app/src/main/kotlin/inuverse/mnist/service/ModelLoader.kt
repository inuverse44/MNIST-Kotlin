package inuverse.mnist.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.spec.LayerFactory
import inuverse.mnist.neural.spec.ModelSpec
import java.io.File

class ModelLoader {
    private val mapper = jacksonObjectMapper()

    /**
     * 新形式 (ModelSpec v1) を読み込み、ネットワークを動的に構築して返す。
     * 旧形式（List<Map<...>>>）を検出した場合は例外で通知する。
     */
    fun loadToNewNetwork(filepath: String, learningRate: Double = 0.01): Network {
        val file = File(filepath)
        if (!file.exists()) {
            throw IllegalArgumentException("Model file not found: $filepath")
        }

        println("🐶Loading model (ModelSpec) from $filepath ...")

        // まず ModelSpec を試みる
        val spec: ModelSpec = mapper.readValue(file)
        val network = LayerFactory.buildNetwork(spec, learningRate)
        println("🐶Model loaded successfully (layers=${spec.layers.size})!")
        return network
    }

    /**
     * 互換用の旧API: 既存ネットワークにパラメタを適用。
     * 新形式のファイルが与えられた場合は、構成が異なるためこのAPIでは扱わず例外にする。
     */
    fun load(filepath: String, network: Network) {
        val file = File(filepath)
        if (!file.exists()) {
            throw IllegalArgumentException("Model file not found: $filepath")
        }

        // 旧形式: [ {"type":"Dense", "params":{...}}, ... ] を読み込む想定
        val anyTree = mapper.readTree(file)
        if (anyTree.has("version") && anyTree.has("layers")) {
            throw IllegalArgumentException("Given file is ModelSpec. Use loadToNewNetwork() to build network dynamically.")
        }

        println("🐶Loading legacy model into existing network from $filepath ...")
        val layersData: List<Map<String, Any>> = mapper.readValue(file)

        val networkLayers = network.getLayers()
        if (layersData.size != networkLayers.size) {
            println("🐶Warning: Layer count mismatch! File: ${layersData.size}, Network: ${networkLayers.size}")
        }

        for ((index, layerData) in layersData.withIndex()) {
            if (index >= networkLayers.size) break
            val layerName = layerData["type"] as String
            val params = layerData["params"] as Map<String, Any>
            val targetLayer = networkLayers[index]
            if (targetLayer.getName() != layerName) {
                println("🐶Warning: Layer type mismatch at index $index. File: $layerName, Network: ${targetLayer.getName()}")
            }
            if (params.isNotEmpty()) {
                targetLayer.loadParameters(params)
            }
        }
        println("🐶Model (legacy) loaded successfully!")
    }
}
