package inuverse.mnist.server

import inuverse.mnist.neural.spec.LayerEntry

data class TrainStartRequest(
    val layers: List<LayerEntry>?,
    val config: TrainConfigReq,
    val save: SaveReq? = null
)

