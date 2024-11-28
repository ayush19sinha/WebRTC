package my.android.webrtc.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import my.android.webrtc.R
import my.android.webrtc.ui.theme.PrimaryColor

@Composable
fun AppOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    labelStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    maxLength: Int? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = labelStyle,
            color = determineTextColor(isError, enabled)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (maxLength == null || newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium) },
            isError = isError,
            enabled = enabled,
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            colors = outlinedTextFieldColors(isError),
            keyboardOptions = keyboardOptions,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            supportingText = supportingTextContent(
                supportingText = supportingText,
                isError = isError,
                currentLength = value.length,
                maxLength = maxLength
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    maxLength: Int = 32,
    showPasswordRequirements: Boolean = false,
    onPasswordVisibilityChanged: ((Boolean) -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = determineTextColor(isError, enabled)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            enabled = enabled,
            isError = isError,
            singleLine = true,
            placeholder = {
                Text(text = placeholder, style = MaterialTheme.typography.bodyMedium)
            },
            shape = MaterialTheme.shapes.medium,
            colors = outlinedTextFieldColors(isError),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            trailingIcon = {
                PasswordVisibilityToggle(
                    passwordVisible = passwordVisible,
                    isError = isError,
                    onToggle = {
                        passwordVisible = !passwordVisible
                        onPasswordVisibilityChanged?.invoke(passwordVisible)
                    }
                )
            },
            supportingText = {
                PasswordSupportingTextContent(
                    supportingText = supportingText,
                    isError = isError,
                    currentLength = value.length,
                    maxLength = maxLength,
                    showPasswordRequirements = showPasswordRequirements,
                    password = value
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        )
    }
}

@Composable
private fun PasswordVisibilityToggle(
    passwordVisible: Boolean,
    isError: Boolean,
    onToggle: () -> Unit
) {
    IconButton(onClick = onToggle) {
        Icon(
            painter = painterResource(
                if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
            ),
            contentDescription = if (passwordVisible) "Hide password" else "Show password",
            tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun supportingTextContent(
    supportingText: String?,
    isError: Boolean,
    currentLength: Int,
    maxLength: Int?
): @Composable (() -> Unit)? {
    return if (!supportingText.isNullOrEmpty() || maxLength != null) {
        {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                supportingText?.let {
                    Text(
                        text = it,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                maxLength?.let {
                    Text(
                        text = "$currentLength/$maxLength",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    } else null
}
@Composable
private fun PasswordSupportingTextContent(
    supportingText: String?,
    isError: Boolean,
    currentLength: Int,
    maxLength: Int,
    showPasswordRequirements: Boolean,
    password: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        supportingText?.let {
            Text(
                text = it,
                color = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showPasswordRequirements) {
            PasswordRequirements(password = password)
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$currentLength/$maxLength",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PasswordRequirements(
    password: String,
    modifier: Modifier = Modifier
) {
    val requirements = listOf(
        RequirementType(
            text = "At least 8 characters",
            check = { it.length >= 8 }
        ),
        RequirementType(
            text = "At least one uppercase letter",
            check = { it.any { char -> char.isUpperCase() } }
        ),
        RequirementType(
            text = "At least one lowercase letter",
            check = { it.any { char -> char.isLowerCase() } }
        ),
        RequirementType(
            text = "At least one number",
            check = { it.any { char -> char.isDigit() } }
        ),
        RequirementType(
            text = "At least one special character",
            check = { it.any { char -> !char.isLetterOrDigit() } }
        )
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        requirements.forEach { requirement ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isMet = requirement.check(password)
                Icon(
                    imageVector = if (isMet)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.Close,
                    contentDescription = if (isMet) "Requirement met" else "Requirement not met",
                    tint = if (isMet)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = requirement.text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class RequirementType(
    val text: String,
    val check: (String) -> Boolean
)

@Composable
private fun determineTextColor(isError: Boolean, enabled: Boolean): Color {
    return when {
        isError -> MaterialTheme.colorScheme.error
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
private fun outlinedTextFieldColors(isError: Boolean) = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    disabledContainerColor = Color.White.copy(alpha = 0.9f),
    focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else PrimaryColor,
    unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline,
    cursorColor = if (isError) MaterialTheme.colorScheme.error else PrimaryColor
)