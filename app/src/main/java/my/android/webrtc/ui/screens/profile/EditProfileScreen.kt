package my.android.webrtc.ui.screens.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import my.android.webrtc.model.MUser
import my.android.webrtc.ui.screens.components.AppOutlinedTextField
import my.android.webrtc.ui.screens.components.AppTopBarWithBackButton
import my.android.webrtc.ui.screens.components.FullWidthButton
import my.android.webrtc.ui.screens.components.FullWidthGrayButton
import my.android.webrtc.ui.theme.PrimaryColor

@Composable
fun EditProfileScreen(
    onSaveClick:()-> Unit,
    onCancelClick:()-> Unit,
    onBackClick:()-> Unit) {

    Scaffold(
        topBar = { AppTopBarWithBackButton(title = "Edit Profile", onBackClick =onBackClick ) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EditProfileContent(user = User1, onSaveClick =  onSaveClick, onCancelClick = onCancelClick )
        }
    }
    
}

@Composable
private fun EditProfileContent(user: MUser, onSaveClick:()-> Unit,
                               onCancelClick:()-> Unit) {

    var name by remember { mutableStateOf(user.name) }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber) }
    var profilePicture by remember { mutableStateOf<android.net.Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            profilePicture = uri
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ProfileHeader(name = name, profilePicture = profilePicture)
            TextButton(
                onClick = {
                    pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text(text = "Change photo", color = PrimaryColor)
            }
        }

        EditProfileFields(
            name = name,
            phoneNumber = "(+91) $phoneNumber",
            onNameChange = { name = it },
            onPhoneChange = { newPhone ->
                phoneNumber = newPhone.removePrefix("(+91) ")
            }
        )

        Column {
            FullWidthButton(
                title = "Save",
                onClick = onSaveClick
                // TODO
            )
            Spacer(modifier = Modifier.height(8.dp))
            FullWidthGrayButton(
                title = "Cancel",
                onClick = onCancelClick
            )
        }
    }
}

@Composable
fun EditProfileFields(
    name: String,
    phoneNumber: String,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppOutlinedTextField(
            label = "Full name",
            value = name,
            onValueChange = onNameChange,
            placeholder = "Enter your full name",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        AppOutlinedTextField(
            label = "Phone number",
            value = phoneNumber,
            onValueChange = onPhoneChange,
            placeholder = "(+91) Phone number",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Preview
@Composable
private fun EditProfileScreenPreview() {
    EditProfileScreen(onSaveClick = { }, onCancelClick = { }) {
    }
}