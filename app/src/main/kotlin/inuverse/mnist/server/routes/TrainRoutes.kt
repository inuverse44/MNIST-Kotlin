package inuverse.mnist.server.routes

import inuverse.mnist.server.dto.TrainStartRequest
import inuverse.mnist.server.service.TrainingManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay

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

    // Server-Sent Events stream for training progress (simple polling-based push)
    get("/api/train/stream") {
        val jobId = call.request.queryParameters["jobId"]
        if (jobId == null) {
            call.respond(mapOf("error" to "jobId is required"))
            return@get
        }
        call.response.cacheControl(CacheControl.NoCache(null))
        call.respondTextWriter(contentType = ContentType.Text.EventStream) {
            // Simple loop: push current status every ~1s until terminal state
            while (true) {
                val status = trainingManager.getStatus(jobId)
                if (status == null) {
                    write("event: error\n")
                    write("data: {\"error\":\"job not found\"}\n\n")
                    flush()
                    break
                }
                // Write data event
                val payload = "{" +
                    "\"jobId\":\"${status.jobId}\"," +
                    "\"state\":\"${status.state}\"," +
                    (status.epoch?.let { "\"epoch\":$it," } ?: "") +
                    (status.loss?.let { "\"loss\":$it," } ?: "") +
                    (status.accuracy?.let { "\"accuracy\":$it," } ?: "") +
                    (status.message?.let { "\"message\":\"$it\"" } ?: "\"message\":null") +
                    "}"
                write("data: $payload\n\n")
                flush()

                if (status.state == "completed" || status.state == "failed") {
                    break
                }
                delay(1000)
            }
        }
    }
}
