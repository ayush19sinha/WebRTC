package my.android.webrtc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.libraries.places.api.Places
import my.android.webrtc.navigation.AppNavigation
import my.android.webrtc.ui.theme.WebRTCTheme
import my.android.webrtc.ui.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePlaces()
        setContent {
            WebRTCTheme {

                AppNavigation(mapViewModel = mapViewModel)
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

