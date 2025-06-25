package com.bbg.securevault.presentation.loginScreen.masterPasswordsDialogs

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bbg.securevault.presentation.passwords.PasswordTextField
import com.bbg.securevault.presentation.passwords.masterPassword.SaveMasterPasswordToFirestore
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Enoklit on 27.06.2025.
 */

@Composable
fun SetMasterPasswordDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    newPassword: String,
    confirmPassword: String,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    context: Context,
    auth: FirebaseAuth,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Set Master Password") },
        text = {
            Column {
                Text("Create your secure Master Password.")
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = "Master Password",
                    password = newPassword,
                    onPasswordChange = onNewPasswordChange
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = "Confirm Master Password",
                    password = confirmPassword,
                    onPasswordChange = onConfirmPasswordChange
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (newPassword == confirmPassword && newPassword.length >= 8) {
                    val user = auth.currentUser
                    if (user != null) {
                        SaveMasterPasswordToFirestore(
                            uid = user.uid,
                            password = newPassword,
                            onSuccess = onSuccess,
                            onError = onError
                        )
                    }
                } else {
                    onError("Passwords do not match or too short.")
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color.White,
        textContentColor = Color.Black,
        titleContentColor = Color.Black,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 6.dp
    )
}