package inuverse.mnist.server.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.healthRoutes() {
    get("/healthz") {
        call.respondText("ok")
    }
}

