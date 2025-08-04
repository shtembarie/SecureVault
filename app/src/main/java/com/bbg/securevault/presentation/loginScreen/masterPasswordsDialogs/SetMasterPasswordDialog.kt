package com.bbg.securevault.presentation.loginScreen.masterPasswordsDialogs

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bbg.securevault.R
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
        title = { Text(stringResource(R.string.set_master_password)) },
        text = {
            Column {
                Text(stringResource(R.string.create_your_secure_master_password))
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = stringResource(R.string.master_password),
                    password = newPassword,
                    onPasswordChange = onNewPasswordChange
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = stringResource(R.string.confirm_master_password),
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
                    onError(context.getString(R.string.passwords_do_not_match_or_too_short))
                }
            }) {
                Text(stringResource(R.string.save))
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