package my.android.webrtc.ui.screens.map

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import my.android.webrtc.R
import my.android.webrtc.ui.screens.components.LocationBottomSheet
import my.android.webrtc.ui.screens.components.MapScreenTopBar
import my.android.webrtc.ui.theme.PrimaryColor
import my.android.webrtc.ui.viewmodel.MapViewModel
import my.android.webrtc.ui.viewmodel.ResponseState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    ) {
    val context = LocalContext.current
    val address by mapViewModel.markerAddressDetail.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showBottomSheet by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    var addressLine by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(23.344, 85.296), 16f)
    }

    val sheetState = rememberModalBottomSheetState()
    val mapLocation by mapViewModel.mapLocation.collectAsStateWithLifecycle()

    fun animateCameraToLocation(location: LatLng) {
        coroutineScope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 16f),
                durationMs = 500
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                mapViewModel.getCurrentLocation(fusedLocationClient) { location ->
                    currentLocation = location
                    location?.let { animateCameraToLocation(it) }
                }
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        delay(1000)
        mapViewModel.checkLocationPermissionAndFetchLocation(
            context = context,
            fusedLocationClient = fusedLocationClient,
            permissionLauncher = permissionLauncher,
            onLocationReceived = { location ->
                location?.let {
                    currentLocation = it
                    animateCameraToLocation(it)
                }
            },
            onPermissionDenied = {
                Toast.makeText(context, "Location permission is required to access your location",
                    Toast.LENGTH_SHORT).show()
            }
        )
    }

    LaunchedEffect(mapLocation) {
        mapLocation?.let { location ->
            animateCameraToLocation(location)
            markerPosition = location
            mapViewModel.getMarkerAddressDetails(location.latitude, location.longitude, context)
        }
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            markerPosition = it
        }
    }

    Scaffold(
        topBar = { MapScreenTopBar(context = context, onError = { error ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(error)
            }}, onPlaceSelected = { latLng ->
            mapViewModel.updateMapLocation(latLng) }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mapViewModel.checkLocationPermissionAndFetchLocation(
                        context = context,
                        fusedLocationClient = fusedLocationClient,
                        permissionLauncher = permissionLauncher,
                        onLocationReceived = { location ->
                            location?.let { animateCameraToLocation(it) }
                        },
                        onPermissionDenied = {
                            Toast.makeText(context, "Location permission is required to access your location",
                                Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                containerColor = Color.White,
                contentColor = PrimaryColor
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_crosshair),
                    contentDescription = "Get current location", Modifier.size(24.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier.fillMaxSize(),
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                onMapLoaded = { isMapReady = true },
                onMapClick = { latLng ->
                    if (isMapReady) {
                        markerPosition = latLng
                        mapViewModel.getMarkerAddressDetails(latLng.latitude, latLng.longitude, context)
                        showBottomSheet = true
                    }
                }
            ) {
                markerPosition?.let { position ->
                    val originalBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_map_marker)
                    val markerSize = 60.dpToPx(context).toInt()
                    val scaledBitmap = originalBitmap.scale(markerSize, markerSize)
                    val descriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                    Marker(
                        state = MarkerState(position = position),
                        icon = descriptor,
                        alpha = 1.0f,
                        zIndex = 1.0f
                    )
                }
            }

            when (address) {
                is ResponseState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ResponseState.Success -> addressLine = (address as ResponseState.Success).data.getAddressLine(0)
                is ResponseState.Error -> {
                    val error = (address as ResponseState.Error).error
                    Log.e("MAP", "Error fetching address: ${error.message}")
                    addressLine = "Error fetching address"
                    LaunchedEffect(error) {
                        snackbarHostState.showSnackbar("Error fetching address. Please try again.")
                    }
                }

                ResponseState.Idle -> {}
            }
        }
    }

    if (showBottomSheet) {
        LocationBottomSheet(
            sheetState = sheetState,
            location = addressLine ?: "Address not available",
            onSave = {
                onConfirm()
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) showBottomSheet = false
                }
            },
            onDismiss = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) showBottomSheet = false
                }
            }
        )
    }
}

fun Int.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}
