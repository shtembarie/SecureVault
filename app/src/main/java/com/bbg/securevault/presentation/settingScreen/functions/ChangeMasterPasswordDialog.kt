package com.bbg.securevault.presentation.settingScreen.functions

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbg.securevault.R
import com.bbg.securevault.domain.PasswordStore
import com.bbg.securevault.domain.local.EncryptedPasswordDatabase
import com.bbg.securevault.domain.local.repository.PasswordRepository
import com.bbg.securevault.presentation.passwords.PasswordTextField
import com.bbg.securevault.presentation.passwords.masterPassword.SaveMasterPasswordToFirestore
import com.bbg.securevault.presentation.passwords.masterPassword.VerifyMasterPassword
import com.bbg.securevault.presentation.passwords.masterPassword.changeDatabasePassword
import com.bbg.securevault.data.models.NotificationType
import com.bbg.securevault.shared.ui.NotificationBanner
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
    var showBanner by remember { mutableStateOf(false) }
    var bannerType by remember { mutableStateOf(NotificationType.SUCCESS) }
    var bannerTitle by remember { mutableStateOf("") }
    var bannerMessage by remember { mutableStateOf("") }

    // Auto-close after 5 seconds
    LaunchedEffect(showBanner) {
        if (showBanner) {
            delay(5000)
            showBanner = false
        }
    }
    if (showBanner) {
        NotificationBanner(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            type = bannerType,
            title = bannerTitle,
            message = bannerMessage,
            onClose = { showBanner = false }
        )
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.change_master_password)) },
        text = {
            Column {
                PasswordTextField(
                    label = stringResource(R.string.current_password),
                    password = currentPasswordInput,
                    onPasswordChange = { currentPasswordInput = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = stringResource(R.string.new_password),
                    password = newPasswordInput,
                    onPasswordChange = { newPasswordInput = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    label = stringResource(R.string.confirm_new_password),
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
                if (currentPasswordInput.isBlank()) {
                    errorMessage = context.getString(R.string.master_password_should_not_be_empty)
                    successMessage = ""
                    return@Button
                }
                VerifyMasterPassword(
                    uid = uid,
                    inputPassword = currentPasswordInput,
                    onSuccess = {
                        if (newPasswordInput == confirmNewPasswordInput && newPasswordInput.length >= 8) {
                            changeDatabasePassword(
                                context = context,
                                oldPassword = currentPasswordInput,
                                newPassword = newPasswordInput,
                                onSuccess = {
                                    SaveMasterPasswordToFirestore(
                                        uid = uid,
                                        password = newPasswordInput,
                                        onSuccess = {
                                            EncryptedPasswordDatabase.resetInstance()  // reset DB instance
                                            PasswordStore.reset()
                                            PasswordStore.masterPassword = newPasswordInput
                                            EncryptedPasswordDatabase.getInstance(
                                                context,
                                                newPasswordInput,
                                            ) // create new db with new password
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.master_password_changed_successfully),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onDismiss()
                                        },
                                        onError = {
                                            errorMessage = it
                                            successMessage = ""
                                        }
                                    )
                                },
                                onError = {
                                    errorMessage = it
                                    successMessage = ""
                                }
                            )
                        } else {
                            errorMessage =
                                context.getString(R.string.new_passwords_do_not_match_or_are_too_short)
                            successMessage = ""
                        }
                    },
                    onError = {
                        errorMessage = it
                        successMessage = ""

                    }
                )
            }) {
                Text(stringResource(R.string.submit))
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
