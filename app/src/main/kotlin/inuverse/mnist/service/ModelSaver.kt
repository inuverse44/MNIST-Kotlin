package inuverse.mnist.service

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import inuverse.mnist.neural.Network
import java.io.File

class ModelSaver {
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    fun save(network: Network, filepath: String) {
        val parameters = network.getModelParameters()
        mapper.writeValue(File(filepath), parameters)
        println("💾 Model saved to $filepath")
    }
}
