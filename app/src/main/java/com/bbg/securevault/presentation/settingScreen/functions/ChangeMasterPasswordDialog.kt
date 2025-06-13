package com.bbg.securevault.presentation.settingScreen.functions

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bbg.securevault.R
import com.bbg.securevault.presentation.helpFiles.TopStatusBarMessage
import com.bbg.securevault.presentation.passwords.PasswordTextField
import com.bbg.securevault.presentation.passwords.masterPassword.SaveMasterPasswordToFirestore
import com.bbg.securevault.presentation.passwords.masterPassword.VerifyMasterPassword
import kotlinx.coroutines.delay

/**
 * Created by Enoklit on 11.06.2025.
 */

@SuppressLint("ResourceAsColor")
@Composable
fun ChangeMasterPasswordDialog(
    uid: String,
    onDismiss: () -> Unit
) {
    var currentPasswordInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmNewPasswordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Master Password") },
        text = {
            Column {
                PasswordTextField(
                    label = "Current Password",
                    password = currentPasswordInput,
                    onPasswordChange = { currentPasswordInput = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = "New Password",
                    password = newPasswordInput,
                    onPasswordChange = { newPasswordInput = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = "Confirm New Password",
                    password = confirmNewPasswordInput,
                    onPasswordChange = { confirmNewPasswordInput = it }
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color.Red, fontSize = 14.sp)
                }
                if (successMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(successMessage, color = Color.Green, fontSize = 14.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                VerifyMasterPassword(
                    uid = uid,
                    inputPassword = currentPasswordInput,
                    onSuccess = {
                        if (newPasswordInput == confirmNewPasswordInput && newPasswordInput.length >= 8) {
                            SaveMasterPasswordToFirestore(
                                uid = uid,
                                password = newPasswordInput,
                                onSuccess = {
                                    Toast.makeText(context, "Master password changed successfully", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                onError = {
                                    errorMessage = it
                                    successMessage = ""
                                }
                            )
                        } else {
                            errorMessage = "New passwords do not match or are too short."
                            successMessage = ""
                        }
                    },
                    onError = {
                        errorMessage = it
                        successMessage = ""
                    }
                )
            }) {
                Text("Submit")
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
