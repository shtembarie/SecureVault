package com.bbg.securevault.presentation.loginScreen.masterPasswordsDialogs

import android.content.Context
import android.os.Build
import android.widget.Toast
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
import com.bbg.securevault.data.objects.EncryptedPrefs.getLoggedInUserId
import com.bbg.securevault.data.objects.EncryptedPrefs.saveLoggedInUserId
import com.bbg.securevault.domain.InactivityManager
import com.bbg.securevault.domain.PasswordStore
import com.bbg.securevault.presentation.passwords.PasswordTextField
import com.bbg.securevault.presentation.passwords.masterPassword.VerifyMasterPassword
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.bbg.securevault.data.models.NotificationType
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
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    val uid = user.uid
                    val storedUserId = getLoggedInUserId(context)  // gespeicherte UserId vom Gerät
                    if (storedUserId != null && storedUserId != uid) {
                        // Ein anderer User ist schon gespeichert → Login verhindern
                        Toast.makeText(
                            context,
                            context.getString(R.string.this_device_is_already_linked_to_another_account_please_log_in_with_your_original_account),
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    VerifyMasterPassword(
                        uid = uid,
                        inputPassword = masterPasswordInput,
                        onSuccess = {
                            val userId = "biometric-${Build.MODEL.hashCode()}"
                            PasswordStore.setAuthenticated(
                                context = context,
                                authenticated = true,
                                password = masterPasswordInput,
                                userId = userId
                            )
                            // UserId speichern, falls noch nicht gespeichert
                            if (storedUserId == null) {
                                saveLoggedInUserId(context, uid)
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                PasswordStore.loadFromDatabase()
                            }
                            InactivityManager.updateActivity()
                            onVerified()
                            navController.navigate("tabs") { popUpTo(0) }
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.user_nicht_eingeloggt),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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