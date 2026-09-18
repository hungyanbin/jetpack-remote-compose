package com.example.composeremote.server

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080) {
            routing {
                get("/doc") {
                    call.respondBytes(bytes = buildDocument(), contentType = ContentType.Application.OctetStream)
                }
            }
        }
        .start(wait = true)
}
