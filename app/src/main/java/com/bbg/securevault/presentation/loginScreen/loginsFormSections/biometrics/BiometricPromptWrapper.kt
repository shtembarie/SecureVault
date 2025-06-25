package com.bbg.securevault.presentation.loginScreen.loginsFormSections.biometrics

import androidx.compose.runtime.Composable
import com.bbg.securevault.presentation.passwords.BiometricAuthPrompt

/**
 * Created by Enoklit on 27.06.2025.
 */

@Composable
fun BiometricPromptWrapper(
    show: Boolean,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (show) {
        BiometricAuthPrompt(
            onAuthSuccess = onSuccess,
            onAuthError = onError
        )
    }
}