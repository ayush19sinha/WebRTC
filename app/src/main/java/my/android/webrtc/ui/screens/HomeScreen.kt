package my.android.webrtc.ui.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import my.android.webrtc.ui.screens.components.AppTopBar

@Composable
fun HomeScreen(){
    Scaffold(
        topBar = {
            AppTopBar(title = "Home")
        }
    ) { innerPadding ->

    }
}


@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}