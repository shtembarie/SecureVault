package com.bbg.securevault.presentation.passwords

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.bbg.securevault.R
import com.bbg.securevault.data.objects.EncryptedPrefs
import com.google.firebase.auth.FirebaseAuth


/**
 * Created by Enoklit on 10.06.2025.
 */

@Composable
fun BiometricAuthPrompt(
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity() as? FragmentActivity ?: return


    LaunchedEffect(Unit) {
        val executor = ContextCompat.getMainExecutor(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.fingerprint_authentication))
            .setSubtitle(context.getString(R.string.authenticate_to_access_your_vault))
            .setNegativeButtonText(context.getString(R.string.abbrechen))
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    val credentials = EncryptedPrefs.loadEmailAndPassword(context)
                    if (credentials != null) {
                        val (email, password) = credentials

                        FirebaseAuth.getInstance()
                            .signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                onAuthSuccess()
                            }
                            .addOnFailureListener {
                                onAuthError(
                                    context.getString(
                                        R.string.authentication_error,
                                        it.message ?: "Unknown error"
                                    )
                                )
                            }
                    } else {
                        onAuthError(context.getString(R.string.no_saved_credentials_found))
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onAuthError(context.getString(R.string.authentication_error, errString))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onAuthError(context.getString(R.string.fingerprint_not_recognized_try_again))
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
