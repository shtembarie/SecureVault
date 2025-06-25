package com.bbg.securevault.domain.googlesigninandup

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.bbg.securevault.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider


/**
 * Created by Enoklit on 27.06.2025.
 */
object GoogleAuthManager {
    private const val TAG = "GoogleAuthManager"

    private var oneTapClient: SignInClient? = null
    private var signInRequest: BeginSignInRequest? = null

    fun initialize(context: Context) {
        oneTapClient = Identity.getSignInClient(context)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun launchGoogleSignIn(
        context: Context,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        isNewUser: Boolean
    ) {
        if (oneTapClient == null || signInRequest == null) {
            initialize(context)
        }

        oneTapClient?.beginSignIn(signInRequest!!)
            ?.addOnSuccessListener { result ->
                val request = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(request)

            }
            ?.addOnFailureListener { e ->
                Log.e(TAG, "Google Sign-In failed: ${e.localizedMessage}")
            }
    }

    fun handleGoogleSignInResult(
        context: Context,
        intent: Intent?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google account obtained: ${account.email}")

            val idToken = account.idToken
            if (idToken == null) {
                Log.e(TAG, "Google ID Token is null")
                onError("Google ID Token is null")
                return
            }

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Log.d(TAG, "Firebase signInWithCredential success")
                        onSuccess()
                    } else {
                        Log.e(TAG, "Firebase signInWithCredential failed", authTask.exception)
                        onError(authTask.exception?.localizedMessage ?: "Firebase sign-in failed")
                    }
                }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign-in failed: ${e.statusCode}", e)
            onError("Google sign-in failed: ${e.statusCode}")
        }
    }


}