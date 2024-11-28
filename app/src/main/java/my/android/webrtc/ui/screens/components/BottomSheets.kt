package my.android.webrtc.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import my.android.webrtc.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LocationBottomSheet(
    sheetState: SheetState,
    location: String,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    var houseNumber by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = MaterialTheme.shapes.medium,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 80.dp,
                height = 5.dp,
                color = Color.LightGray
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .imePadding()
                .imeNestedScroll()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = location,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppOutlinedTextField(
                label = "Block/floor/room/house number",
                value = houseNumber,
                onValueChange = { houseNumber = it },
                placeholder = "",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                labelStyle = MaterialTheme.typography.bodyLarge
            )

            AppOutlinedTextField(
                label = "Landmark",
                value = landmark,
                onValueChange = { landmark = it },
                placeholder = "",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                labelStyle = MaterialTheme.typography.bodyLarge
            )

            FullWidthButton(title = "Save", onClick = onSave)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutBottomSheet(
    sheetState: SheetState,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RectangleShape,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                width = 80.dp,
                height = 5.dp,
                color = Color.LightGray
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Logging out?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )
            AsyncImage(
                model = R.drawable.logout_thank_you,
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Thanks for stopping by.", style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp))

                Text(text = "See you again soon!", style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp))
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FullWidthButton(
                    title = "Logout",
                    onClick = onLogout,
                )
                FullWidthGrayButton(
                    title = "Cancel",
                    onClick = onDismiss,
                )
            }
        }
    }
}