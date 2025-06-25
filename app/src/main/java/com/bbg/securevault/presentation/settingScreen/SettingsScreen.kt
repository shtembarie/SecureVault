package com.bbg.securevault.presentation.settingScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.domain.BiometricSettingsStore
import com.bbg.securevault.presentation.passwords.BiometricAuthPrompt
import com.bbg.securevault.presentation.settingScreen.cards.Section
import com.bbg.securevault.presentation.settingScreen.cards.SettingItem
import com.bbg.securevault.presentation.settingScreen.cards.ToggleItem
import com.bbg.securevault.presentation.settingScreen.functions.ChangeMasterPasswordDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Created by Enoklit on 04.06.2025.
 */

@Composable
fun SettingsScreen(
    navController: NavController,
    logout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val biometricEnabled by BiometricSettingsStore.isEnabledFlow(context).collectAsState(initial = false)
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    var showBiometricPrompt by remember { mutableStateOf(false) }
    var pendingToggleValue by remember { mutableStateOf(biometricEnabled) } // we store the intent
    var biometricError by remember { mutableStateOf<String?>(null) }



    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(16.dp)) {

        item {
            Section(title = "Security") {
            SettingItem(
                icon = { Icon(Icons.Default.Lock, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = "Change Master Password",
                subtitle = "Update your master password",
                onClick = { showChangePasswordDialog = true }
            )
            if (showChangePasswordDialog) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    ChangeMasterPasswordDialog(
                        uid = user.uid,
                        onDismiss = { showChangePasswordDialog = false }
                    )
                }
            }
                ToggleItem(
                    icon = {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = colorResource(R.color.primary))
                    },
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face ID to unlock",
                    value = biometricEnabled,
                    onToggle = {
                        // First trigger biometric prompt
                        pendingToggleValue = !biometricEnabled
                        showBiometricPrompt = true
                    }
                )
                if (showBiometricPrompt) {
                    BiometricAuthPrompt(
                        onAuthSuccess = {
                            scope.launch {
                                BiometricSettingsStore.setEnabled(context, pendingToggleValue)
                                showBiometricPrompt = false
                                biometricError = null
                            }
                        },
                        onAuthError = { error ->
                            showBiometricPrompt = false
                            biometricError = error // show this under the toggle or via snackbar
                        }
                    )
                }
                biometricError?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }



                SettingItem(
                icon = { Icon(Icons.Default.Shield, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = "Security Audit",
                subtitle = "Check for weak or reused passwords",
                onClick = { showNotImplemented(context) }
            )
        }

        Section(title = "Preferences") {
            ToggleItem(
                icon = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = "Dark Mode",
                subtitle = stringResource(R.string.switch_between_light_and_dark_theme),
                value = false,
                onToggle = { showNotImplemented(context) }
            )

            ToggleItem(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = "Notifications",
                subtitle = "Enable password breach alerts",
                value = true,
                onToggle = { showNotImplemented(context) }
            )
        }

        Section(title = "Data Management") {
            SettingItem(
                icon = { Icon(Icons.Default.Download, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = "Export Passwords",
                subtitle = "Export your passwords as an encrypted file",
                onClick = { showNotImplemented(context) }
            )

            SettingItem(
                icon = { Icon(Icons.Default.Upload, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = "Import Passwords",
                subtitle = "Import passwords from another manager",
                onClick = { showNotImplemented(context) }
            )

            SettingItem(
                icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = colorResource(R.color.danger)) },
                title = "Delete All Data",
                subtitle = "Permanently delete all passwords",
                onClick = { showNotImplemented(context) },
                destructive = true
            )
        }

        Section(title = "Account") {
            SettingItem(
                icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = colorResource(R.color.danger)) },
                title = "Logout",
                subtitle = "Log out of your account",
                onClick = logout,
                destructive = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text("SecureVault v1.0.0", fontSize = 14.sp, color = colorResource(R.color.textSecondary))
            Text("© 2025 SecureVault", fontSize = 12.sp, color = colorResource(R.color.textSecondary))
        }
    }
    }
}
fun showNotImplemented(context: android.content.Context) {
    Toast.makeText(context, "Not implemented in demo", Toast.LENGTH_SHORT).show()
}
