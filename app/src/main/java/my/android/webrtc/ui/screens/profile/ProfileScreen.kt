package my.android.webrtc.ui.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import my.android.webrtc.R
import my.android.webrtc.model.MUser
import my.android.webrtc.ui.screens.components.AppTopBar
import my.android.webrtc.ui.screens.components.LogoutBottomSheet
import my.android.webrtc.ui.screens.map.dpToPx
import my.android.webrtc.ui.theme.PrimaryColor
import my.android.webrtc.ui.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    mapViewModel: MapViewModel,
    onEditProfileClick: () -> Unit,
    onMapClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showLogoutBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            AppTopBar(title = "Profile")
        }
    ) { innerPadding ->
        ProfileContent(
            mapViewModel = mapViewModel,
            paddingValues = innerPadding,
            user = User1,
            onEditProfileClick = onEditProfileClick,
            onDeleteAccountClick = { showLogoutBottomSheet = true },
            onMapClick = onMapClick
        )
    }
    if (showLogoutBottomSheet){
        LogoutBottomSheet(sheetState = sheetState,

            onLogout = {
                showLogoutBottomSheet = false
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show() },

            onDismiss = {showLogoutBottomSheet = false})
    }
}

@Composable
private fun ProfileContent(
    paddingValues: PaddingValues,
    mapViewModel: MapViewModel,
    user: MUser,
    onEditProfileClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onMapClick: () -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


    LaunchedEffect(Unit) {
        mapViewModel.getCurrentLocation(fusedLocationClient) { location ->
            location?.let {
                currentLocation = it
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 16f)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
            .padding(vertical = 20.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ProfileHeader(name = user.name, profilePicture = null)
            EditProfileButton(onClick = onEditProfileClick)
        }
        item {
            ProfileMenus(
                user = user,
                onLogoutClick = onDeleteAccountClick,
                cameraPosition = cameraPositionState,
                markerPosition = currentLocation,
                onMapClick = onMapClick
            )
        }
    }
}

@Composable
fun ProfileHeader(name: String, profilePicture: android.net.Uri?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profilePicture ?: R.drawable.avatar)
                .crossfade(true)
                .build(),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EditProfileButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Edit Profile",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Text(text = "Edit Profile", color = Color.White)
        }
    }
}

@Composable
private fun ProfileMenus(
    user: MUser,
    onLogoutClick: () -> Unit,
    cameraPosition: CameraPositionState,
    markerPosition: LatLng?,
    onMapClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        MenuRow(heading = "Full name", value = user.name)
        MenuRow(heading = "Phone number", value = "+91 ${user.phoneNumber}")
        MenuRow(heading = "Email", value = user.email)
        SmallMap(cameraPosition = cameraPosition, onMapClick = onMapClick,
            markerPosition = markerPosition)
        PrivacySeparator()
        LogoutButton(onClick = onLogoutClick)
    }
}

@Composable
fun SmallMap(
    cameraPosition: CameraPositionState,
    markerPosition: LatLng?,
    onMapClick: () -> Unit,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            GoogleMap(
                cameraPositionState = cameraPosition,
                modifier = Modifier
                    .fillMaxSize(),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                markerPosition?.let {
                    val originalBitmap =
                        BitmapFactory.decodeResource(context.resources, R.drawable.ic_map_marker)
                    val markerSize = 60.dpToPx(context).toInt()
                    val scaledBitmap =
                        Bitmap.createScaledBitmap(originalBitmap, markerSize, markerSize, false)
                    val descriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                    Marker(
                        state = MarkerState(position = it),
                        icon = descriptor,
                        alpha = 1.0f,
                        zIndex = 1.0f
                    )
                }
            }

            IconButton(
                onClick = onMapClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.fullscreen),
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PrivacySeparator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = "Privacy",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun MenuRow(
    heading: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = heading,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Logout",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.error
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Logout",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

val User1 = MUser(
    id = "1",
    name = "Ayush Sinha",
    phoneNumber = "84894582342",
    email = "ayush@gmail.com",
    address = "Ranchi, Jharkhand"
)

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        mapViewModel = MapViewModel(),
        onEditProfileClick = {  },
        onMapClick = {}
    )
}