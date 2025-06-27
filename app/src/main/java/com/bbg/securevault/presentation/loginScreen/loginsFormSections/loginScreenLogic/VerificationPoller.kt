package com.bbg.securevault.presentation.loginScreen.loginsFormSections.loginScreenLogic


import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseUser

import kotlinx.coroutines.delay

/**
 * Created by Enoklit on 27.06.2025.
 */
@Composable
fun VerificationPoller(
    showVerificationWaiting: Boolean,
    user: FirebaseUser?,
    onVerified: () -> Unit,
    onNotVerified: () -> Unit
) {
    var isVerified by remember { mutableStateOf(false) }

    LaunchedEffect(showVerificationWaiting) {
        if (showVerificationWaiting) {
            while (!isVerified) {
                delay(1000)
                user?.reload()
                isVerified = user?.isEmailVerified == true
                if (isVerified) onVerified() else onNotVerified()
            }
        }
    }
}
