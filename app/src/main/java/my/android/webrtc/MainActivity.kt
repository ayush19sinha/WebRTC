package my.android.webrtc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.libraries.places.api.Places
import my.android.webrtc.ui.screens.MapScreen
import my.android.webrtc.ui.theme.WebRTCTheme
import my.android.webrtc.ui.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePlaces()

        setContent {
            WebRTCTheme {
                MapScreen(mapViewModel = MapViewModel())
            }
        }
    }
    private fun initializePlaces() {
        if (!Places.isInitialized()) {
            val apiKey = BuildConfig.MAPS_API_KEY
            Places.initialize(applicationContext, apiKey)
        }
    }
}

