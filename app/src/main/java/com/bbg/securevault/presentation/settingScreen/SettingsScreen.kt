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
                title = stringResource(R.string.change_master_password),
                subtitle = stringResource(R.string.update_your_master_password),
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
                    title = stringResource(R.string.biometric_authentication),
                    subtitle = stringResource(R.string.use_fingerprint_or_face_id_to_unlock),
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
                title = stringResource(R.string.security_audit),
                subtitle = stringResource(R.string.check_for_weak_or_reused_passwords),
                onClick = { showNotImplemented(context) }
            )
        }

        Section(title = "Preferences") {
            ToggleItem(
                icon = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = stringResource(R.string.dark_mode),
                subtitle = stringResource(R.string.switch_between_light_and_dark_theme),
                value = false,
                onToggle = { showNotImplemented(context) }
            )

            ToggleItem(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = stringResource(R.string.notifications),
                subtitle = stringResource(R.string.enable_password_breach_alerts),
                value = true,
                onToggle = { showNotImplemented(context) }
            )
        }

        Section(title = stringResource(R.string.data_management)) {
            SettingItem(
                icon = { Icon(Icons.Default.Download, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = stringResource(R.string.export_passwords),
                subtitle = stringResource(R.string.export_your_passwords_as_an_encrypted_file),
                onClick = { showNotImplemented(context) }
            )

            SettingItem(
                icon = { Icon(Icons.Default.Upload, contentDescription = null, tint = colorResource(R.color.primary)) },
                title = stringResource(R.string.import_passwords),
                subtitle = stringResource(R.string.import_passwords_from_another_manager),
                onClick = { showNotImplemented(context) }
            )

            SettingItem(
                icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = colorResource(R.color.danger)) },
                title = stringResource(R.string.delete_all_data),
                subtitle = stringResource(R.string.permanently_delete_all_passwords),
                onClick = { showNotImplemented(context) },
                destructive = true
            )
        }

        Section(title = stringResource(R.string.account)) {
            SettingItem(
                icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = colorResource(R.color.danger)) },
                title = stringResource(R.string.logout),
                subtitle = stringResource(R.string.log_out_of_your_account),
                onClick = logout,
                destructive = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.securevault_v1_0_0), fontSize = 14.sp, color = colorResource(R.color.textSecondary))
            Text(stringResource(R.string._2025_securevault), fontSize = 12.sp, color = colorResource(R.color.textSecondary))
        }
    }
    }
}
fun showNotImplemented(context: android.content.Context) {
    Toast.makeText(context, context.getString(R.string.not_implemented_in_demo), Toast.LENGTH_SHORT).show()
}
