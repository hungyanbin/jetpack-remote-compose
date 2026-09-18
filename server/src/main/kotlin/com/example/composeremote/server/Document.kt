package com.example.composeremote.server

import androidx.compose.remote.core.CoreDocument
import androidx.compose.remote.core.RcProfiles
import androidx.compose.remote.core.operations.Header
import androidx.compose.remote.creation.JvmRcPlatformServices
import androidx.compose.remote.creation.RemoteComposeWriter
import androidx.compose.remote.creation.dsl.Modifier
import androidx.compose.remote.creation.dsl.RcProfile
import androidx.compose.remote.creation.dsl.background
import androidx.compose.remote.creation.dsl.createRcBuffer
import androidx.compose.remote.creation.dsl.fillMaxSize
import androidx.compose.remote.creation.dsl.rsp
import androidx.compose.remote.creation.dsl.size
import androidx.compose.remote.creation.profile.Profile
import androidx.compose.remote.creation.profile.RemoteComposeWriterFactory

private const val WHITE = 0xFFFFFFFF.toInt()
private const val GRAY = 0xFF888888.toInt()
private const val RED = 0xFFE53935.toInt()

// There's no ready-made "run on a plain JVM" profile in the library (the ANDROIDX one baked into
// RcPlatformProfiles requires android.* classes), so this replicates its configuration -
// same API level, same androidx.* operations bitmask, same wire format - with a JVM-only platform.
private val jvmAndroidxProfile =
    Profile(
        CoreDocument.DOCUMENT_API_LEVEL,
        RcProfiles.PROFILE_ANDROIDX,
        JvmRcPlatformServices(),
        RemoteComposeWriterFactory { creationDisplayInfo, profile, writerCallback ->
            RemoteComposeWriter(creationDisplayInfo, null, profile, writerCallback)
        },
    )

private const val DOCUMENT_WIDTH = 400
private const val DOCUMENT_HEIGHT = 800

fun buildDocument(): ByteArray =
    createRcBuffer(
        RcProfile(jvmAndroidxProfile),
        RemoteComposeWriter.hTag(Header.DOC_WIDTH, DOCUMENT_WIDTH),
        RemoteComposeWriter.hTag(Header.DOC_HEIGHT, DOCUMENT_HEIGHT),
    ) {
        Column(modifier = Modifier.fillMaxSize().background(WHITE)) {
            Text("Hello from the server!", fontSize = 28.rsp)
            Text("Rendered natively via RemoteCompose", color = GRAY)
            Box(modifier = Modifier.size(80f).background(RED))
        }
    }
