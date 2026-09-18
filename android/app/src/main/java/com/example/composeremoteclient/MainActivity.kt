package com.example.composeremoteclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.composeremoteclient.theme.ComposeRemoteClientTheme
import com.example.composeremoteclient.ui.RemoteUiScreen

// 10.0.2.2 is the emulator's alias for the host machine's localhost.
// Point this at your machine's LAN IP instead when running on a physical device.
private const val SERVER_URL = "http://10.0.2.2:8080/doc"

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      ComposeRemoteClientTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          RemoteUiScreen(serverUrl = SERVER_URL)
        }
      }
    }
  }
}
