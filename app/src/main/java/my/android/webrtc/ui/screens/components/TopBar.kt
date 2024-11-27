package my.android.webrtc.ui.screens.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.android.webrtc.ui.theme.PrimaryColor

@Composable
fun MapScreenTopBar(
    context: Context,
    onPlaceSelected: (LatLng) -> Unit,
    onError: (String) -> Unit
) {
    var locationQuery by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var showPredictions by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    val placesClient = remember { Places.createClient(context) }

    fun getPlacePredictions(query: String) {
        if (query.length >= 3) {
            scope.launch(Dispatchers.IO) {
                try {
                    val token = AutocompleteSessionToken.newInstance()
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(query)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            predictions = response.autocompletePredictions
                            showPredictions = true
                            isSearching = false
                        }
                        .addOnFailureListener { exception ->
                            onError("Place prediction failed: ${exception.message}")
                            isSearching = false
                        }
                } catch (e: Exception) {
                    onError("Error: ${e.message}")
                    isSearching = false
                }
            }
        } else {
            predictions = emptyList()
            showPredictions = false
        }
    }

    fun getPlaceDetails(placeId: String) {
        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                place.latLng?.let { latLng ->
                    onPlaceSelected(latLng)
                    locationQuery = place.name ?: ""
                    showPredictions = false
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
            .addOnFailureListener { exception ->
                onError("Place details failed: ${exception.message}")
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryColor)
    ) {
        Column {
            TextField(
                value = locationQuery,
                onValueChange = { query ->
                    locationQuery = query
                    isSearching = true
                    getPlacePredictions(query)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(18.dp)),
                placeholder = { Text("Search for a place") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                trailingIcon = {
                    if (locationQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            modifier = Modifier.clickable {
                                locationQuery = ""
                                predictions = emptyList()
                                showPredictions = false
                            }
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            if (showPredictions && predictions.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    LazyColumn {
                        items(predictions) { prediction ->
                            PredictionItem(
                                prediction = prediction,
                                onClick = {
                                    getPlaceDetails(prediction.placeId)
                                }
                            )
                        }
                    }
                }
            }

            if (isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    color = PrimaryColor
                )
            }
        }
    }
}

@Composable
private fun PredictionItem(
    prediction: AutocompletePrediction,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = prediction.getPrimaryText(null).toString(),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = prediction.getSecondaryText(null).toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}