package inuverse.mnist.server.routes

import inuverse.mnist.server.dto.TrainStartRequest
import inuverse.mnist.server.service.TrainingManager
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*

fun Routing.trainRoutes(trainingManager: TrainingManager) {
    post("/api/train/start") {
        val req = call.receive<TrainStartRequest>()
        val jobId = trainingManager.startTraining(req)
        call.respond(mapOf("jobId" to jobId))
    }

    get("/api/train/status") {
        val jobId = call.request.queryParameters["jobId"]
        if (jobId == null) {
            call.respond(mapOf("error" to "jobId is required"))
            return@get
        }
        val status = trainingManager.getStatus(jobId)
        if (status == null) {
            call.respond(mapOf("error" to "job not found"))
            return@get
        }
        call.respond(status)
    }
}
