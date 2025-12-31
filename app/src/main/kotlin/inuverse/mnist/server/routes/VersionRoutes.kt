package inuverse.mnist.server.routes

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Routing.versionRoutes(modelPath: String, json: ObjectMapper) {
    get("/version") {
        val modelFile = File(modelPath)
        val exists = modelFile.exists()
        val info = mutableMapOf<String, Any>(
            "modelPath" to modelPath,
            "exists" to exists
        )
        if (exists) {
            info["size"] = modelFile.length()
            runCatching {
                val tree = json.readTree(modelFile)
                if (tree.has("version")) {
                    info["modelSpecVersion"] = tree.get("version").asText()
                }
            }
        }
        call.respond(info)
    }
}

