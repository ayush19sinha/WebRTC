package my.android.webrtc.ui.viewmodel


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class MapViewModel : ViewModel() {
    private val _markerAddressDetail = MutableStateFlow<ResponseState<Address>>(ResponseState.Idle)
    val markerAddressDetail: StateFlow<ResponseState<Address>> = _markerAddressDetail.asStateFlow()

    private val _mapLocation = MutableStateFlow<LatLng?>(null)
    val mapLocation: StateFlow<LatLng?> = _mapLocation.asStateFlow()

    fun updateMapLocation(latLng: LatLng) {
        _mapLocation.value = latLng
    }

    fun getMarkerAddressDetails(lat: Double, long: Double, context: Context) {
        viewModelScope.launch {
            _markerAddressDetail.value = ResponseState.Loading
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, long, 1) { addresses ->
                        _markerAddressDetail.value = if (addresses.isNotEmpty()) {
                            ResponseState.Success(addresses[0])
                        } else {
                            ResponseState.Error(Exception("No address found"))
                        }
                    }
                } else {
                    val addresses = geocoder.getFromLocation(lat, long, 1)
                    _markerAddressDetail.value = if (!addresses.isNullOrEmpty()) {
                        ResponseState.Success(addresses[0])
                    } else {
                        ResponseState.Error(Exception("No address found"))
                    }
                }
            } catch (e: Exception) {
                _markerAddressDetail.value = ResponseState.Error(e)
            }
        }
    }

    fun checkLocationPermissionAndFetchLocation(
        context: Context,
        fusedLocationClient: FusedLocationProviderClient,
        permissionLauncher: ActivityResultLauncher<String>,
        onLocationReceived: (LatLng?) -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation(fusedLocationClient, onLocationReceived)
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            onPermissionDenied()
        }
    }

    fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient,
        onLocationReceived: (LatLng?) -> Unit
    ) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(LatLng(it.latitude, it.longitude))
                } ?: run {
                    Log.e("MAP", "Location is null")
                    onLocationReceived(null)
                }
            }.addOnFailureListener { e ->
                Log.e("MAP", "Error getting location", e)
                onLocationReceived(null)
            }
        } catch (e: SecurityException) {
            Log.e("MAP", "Security exception when accessing location", e)
            onLocationReceived(null)
        }
    }
}

sealed class ResponseState<out T> {
    object Idle : ResponseState<Nothing>()
    object Loading : ResponseState<Nothing>()
    data class Error(val error: Throwable) : ResponseState<Nothing>()
    data class Success<T>(val data: T) : ResponseState<T>()
}