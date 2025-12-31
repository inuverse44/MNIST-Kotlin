package inuverse.mnist.server.dto

import inuverse.mnist.neural.spec.LayerEntry

data class TrainStartRequest(
    val layers: List<LayerEntry>?,
    val config: inuverse.mnist.server.dto.TrainConfigReq,
    val save: inuverse.mnist.server.dto.SaveReq? = null
)
