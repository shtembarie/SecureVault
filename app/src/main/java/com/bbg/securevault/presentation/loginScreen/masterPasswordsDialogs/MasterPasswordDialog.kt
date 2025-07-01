package com.bbg.securevault.presentation.loginScreen.masterPasswordsDialogs

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.domain.InactivityManager
import com.bbg.securevault.domain.PasswordStore
import com.bbg.securevault.presentation.passwords.PasswordTextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
/**
 * Created by Enoklit on 27.06.2025.
 */

@Composable
fun MasterPasswordDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    masterPasswordInput: String,
    onPasswordChange: (String) -> Unit,
    context: Context,
    navController: NavController,
    onVerified: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = {},
        title = { Text(stringResource(R.string.security_check)) },
        text = {
            Column {
                Text(stringResource(R.string.enter_your_master_password_to_proceed))
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = stringResource(R.string.master_password),
                    password = masterPasswordInput,
                    onPasswordChange = onPasswordChange
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val userId = "biometric-${Build.MODEL.hashCode()}"
                PasswordStore.setAuthenticated(
                    context = context,
                    authenticated = true,
                    password = masterPasswordInput,
                    userId = userId
                )
                CoroutineScope(Dispatchers.Main).launch {
                    PasswordStore.loadFromDatabase()
                }
                InactivityManager.updateActivity()
                onVerified()
                navController.navigate("tabs") { popUpTo(0) }
            }) {
                Text(stringResource(R.string.verify))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.abbrechen))
            }
        },
        containerColor = Color.White,
        textContentColor = Color.Black,
        titleContentColor = Color.Black,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 6.dp
    )
}