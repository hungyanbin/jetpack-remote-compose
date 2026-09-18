package com.example.composeremoteclient.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.remote.player.view.RemoteComposePlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

private val httpClient = OkHttpClient()

private sealed interface RemoteUiState {
    data object Loading : RemoteUiState

    data class Error(val message: String) : RemoteUiState

    data class Loaded(val bytes: ByteArray) : RemoteUiState
}

// RemoteComposePlayer is @RestrictTo(LIBRARY_GROUP) - not a stable public API - but it's the
// only way to render a RemoteCompose document today.
@Suppress("RestrictedApi")
@Composable
fun RemoteUiScreen(serverUrl: String, modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf<RemoteUiState>(RemoteUiState.Loading) }

    LaunchedEffect(serverUrl) {
        state = RemoteUiState.Loading
        state =
            try {
                RemoteUiState.Loaded(withContext(Dispatchers.IO) { fetchDocument(serverUrl) })
            } catch (e: Exception) {
                RemoteUiState.Error(e.message ?: e.toString())
            }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val current = state) {
            RemoteUiState.Loading -> CircularProgressIndicator()
            is RemoteUiState.Error ->
                Text(
                    text = "Failed to load remote UI from $serverUrl:\n${current.message}",
                    modifier = Modifier.padding(24.dp),
                )
            is RemoteUiState.Loaded ->
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context -> RemoteComposePlayer(context) },
                    update = { player -> player.setDocument(current.bytes) },
                )
        }
    }
}

private fun fetchDocument(url: String): ByteArray {
    val request = Request.Builder().url(url).build()
    httpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) error("HTTP ${response.code}")
        return response.body?.bytes() ?: error("Empty response body")
    }
}
