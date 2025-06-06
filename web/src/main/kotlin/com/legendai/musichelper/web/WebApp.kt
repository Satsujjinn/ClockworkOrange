package com.legendai.musichelper.web

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.*
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondText
import io.ktor.server.response.respondBytes
import io.ktor.http.ContentType
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        install(ContentNegotiation) { json() }
        routing {
            get("/") {
                call.respondHtml {
                    body {
                        h1 { +"MusicGen Helper Web" }
                        form(action = "/generate", method = FormMethod.post) {
                            p {
                                label { +"Prompt:" }
                                textInput(name = "prompt")
                            }
                            p {
                                label { +"Duration:" }
                                numberInput(name = "duration") {
                                    value = "30"
                                    min = "5"
                                    max = "60"
                                }
                            }
                            p {
                                submitInput { value = "Generate" }
                            }
                        }
                    }
                }
            }
            post("/generate") {
                val params = call.receiveParameters()
                val prompt = params["prompt"] ?: return@post call.respondText("Missing prompt")
                val duration = params["duration"]?.toIntOrNull() ?: 30
                val apiKey = System.getenv("MUSICGEN_API_KEY") ?: ""
                val json = Json.encodeToString(
                    GenerateSongRequest(
                        inputs = prompt,
                        parameters = Parameters(duration = duration)
                    )
                )
                val body = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("https://api-inference.huggingface.co/models/facebook/musicgen-small")
                    .post(body)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                val client = OkHttpClient()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@post call.respondText("API error: ${'$'}{response.code}")
                    }
                    val bytes = response.body?.bytes() ?: return@post call.respondText("Empty body")
                    call.respondBytes(bytes, ContentType.parse("audio/wav"))
                }
            }
        }
    }.start(wait = true)
}
