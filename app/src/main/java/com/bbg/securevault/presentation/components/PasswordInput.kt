package com.bbg.securevault.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.bbg.securevault.R
/**
 * Created by Enoklit on 04.06.2025.
 */
@Composable
fun PasswordInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Password",
    autoFocus: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = colorResource(R.color.placeholder)) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        textStyle = LocalTextStyle.current.copy(color = colorResource(R.color.text)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(R.color.primary),
            unfocusedBorderColor = colorResource(R.color.border),
            cursorColor = colorResource(R.color.primary),
            focusedContainerColor = colorResource(R.color.background),
            unfocusedContainerColor = colorResource(R.color.background)
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val icon = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            val description = if (passwordVisible) "Hide password" else "Show password"

            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = colorResource(R.color.textSecondary),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { passwordVisible = !passwordVisible }
            )
        },
        keyboardOptions = KeyboardOptions(autoCorrect = false, capitalization = KeyboardCapitalization.None),
        keyboardActions = KeyboardActions.Default
    )
}

