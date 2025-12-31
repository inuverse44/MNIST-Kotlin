package inuverse.mnist.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inuverse.mnist.neural.Network
import inuverse.mnist.neural.layer.Dense
import inuverse.mnist.neural.layer.Layer
import inuverse.mnist.neural.spec.LayerEntry
import inuverse.mnist.neural.spec.ModelSpec
import java.io.File
import org.slf4j.LoggerFactory

class ModelSaver {
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }
    private val logger = LoggerFactory.getLogger(ModelSaver::class.java)

    /**
     * ネットワークのアーキテクチャとパラメタを JSON (ModelSpec v1) として保存する。
     */
    fun save(network: Network, filepath: String) {
        val entries = mutableListOf<LayerEntry>()
        for (layer: Layer in network.getLayers()) {
            when (layer) {
                is Dense -> {
                    val w = layer.w.getData()
                    val b = layer.b.getData()
                    entries.add(
                        LayerEntry(
                            type = "Dense",
                            inputSize = layer.inputSize,
                            outputSize = layer.outputSize,
                            weights = w,
                            biases = b
                        )
                    )
                }
                else -> {
                    // 活性化関数など（ReLU, Softmax, Sigmoid）
                    entries.add(LayerEntry(type = layer.getName()))
                }
            }
        }

        val spec = ModelSpec(version = "1", layers = entries)
        mapper.writeValue(File(filepath), spec)
        logger.info("Model saved to $filepath (ModelSpec v1)")
    }
}
