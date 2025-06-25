package com.bbg.securevault.presentation.loginScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bbg.securevault.R
import com.bbg.securevault.domain.BiometricSettingsStore
import com.bbg.securevault.presentation.loginScreen.loginsFormSections.VerificationWaitingScreen
import com.bbg.securevault.presentation.loginScreen.loginsFormSections.biometrics.BiometricPromptWrapper
import com.bbg.securevault.presentation.loginScreen.loginsFormSections.biometrics.LoginFormSection
import com.bbg.securevault.presentation.loginScreen.loginsFormSections.loginScreenLogic.VerificationPoller
import com.bbg.securevault.presentation.loginScreen.masterPasswordsDialogs.MasterPasswordDialog
import com.bbg.securevault.presentation.loginScreen.masterPasswordsDialogs.SetMasterPasswordDialog
import com.bbg.securevault.presentation.passwords.LoadingIndicator
import com.google.firebase.auth.FirebaseAuth
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import com.bbg.securevault.domain.googlesigninandup.GoogleAuthManager
import com.bbg.securevault.presentation.passwords.masterPassword.HasMasterPassword

/**
 * Created by Enoklit on 05.06.2025.
 */
@SuppressLint("ResourceAsColor")
@Composable
fun LoginScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val context = LocalContext.current
    val biometricEnabled by BiometricSettingsStore.isEnabledFlow(context).collectAsState(initial = false)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isNewUser by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var infoMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    var showBiometricPrompt by remember { mutableStateOf(false) }
    var showSetMasterPasswordDialog by remember { mutableStateOf(false) }
    var newMasterPassword by remember { mutableStateOf("") }
    var confirmMasterPassword by remember { mutableStateOf("") }
    var masterPasswordInput by remember { mutableStateOf("") }
    var showMasterPasswordDialog by remember { mutableStateOf(false) }

    var hasPromptedBiometric by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    var showVerificationWaiting by remember { mutableStateOf(false) } // Zwischenbildschirm nach Account-Erstellung
    var isVerified by remember { mutableStateOf(false) }
    val user = auth.currentUser

    var googleUserEmail by remember { mutableStateOf<String?>(null) }
    var checkedMasterPassword by remember { mutableStateOf(false) }


    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            GoogleAuthManager.handleGoogleSignInResult(
                context = context,
                intent = intent,
                onSuccess = {
                    Log.d("Login", "Google Sign-In successful")
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        googleUserEmail = currentUser.email
                        checkedMasterPassword = false // Reset für neue Prüfung
                    } else {
                        error = "Fehler beim Lesen der Nutzer-Email"
                    }
                },
                onError = {
                    // ❌ Handle error
                    Log.e("Login", "Google Sign-In error: $it")
                }
            )
        }
    }
    // Wenn googleUserEmail gesetzt ist und noch nicht geprüft wurde
    if (googleUserEmail != null && !checkedMasterPassword) {
        LaunchedEffect(googleUserEmail) {
            // hole den User von FirebaseAuth für die UID
            val user = auth.currentUser
            if (user != null) {
                HasMasterPassword(
                    uid = user.uid,
                    onResult = { hasMasterPassword ->
                        if (hasMasterPassword) {
                            showMasterPasswordDialog = true
                        } else {
                            showSetMasterPasswordDialog = true
                        }
                        checkedMasterPassword = true
                    },
                    onError = { errMsg ->
                        error = errMsg
                        checkedMasterPassword = true
                    }
                )
            } else {
                error = "User nicht eingeloggt"
                checkedMasterPassword = true
            }
        }
    }



    fun resetMessages() {
        error = ""
        infoMessage = ""
    }
    fun handleLogin() {
        resetMessages()

        if (email.isBlank() || password.isBlank()) {
            error = "Please enter both email and password"
            return
        }

        loading = true

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user?.isEmailVerified == true) {
                        showMasterPasswordDialog = true
                    } else {
                        error = "Email not verified. Please check your inbox."
                    }
                } else {
                    error = "Login failed: ${task.exception?.localizedMessage}"
                }
            }
    }
    fun handleCreateAccount() {
        resetMessages()

        when {
            email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                error = "All fields are required"
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                error = "Invalid email format"
                return
            }
            password.length < 8 -> {
                error = "Password must be at least 8 characters"
                return
            }
            password != confirmPassword -> {
                error = "Passwords do not match"
                return
            }
            else -> {
                loading = true
                auth.createUserWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        loading = false
                        if (task.isSuccessful) {
                            auth.currentUser?.sendEmailVerification()
                            // Hier: Warte auf Verifizierung mit Polling
                            showVerificationWaiting = true
                            isNewUser = false
                        } else {
                            error = "Account creation failed: ${task.exception?.localizedMessage}"
                        }
                    }
            }
        }
    }
    fun manualVerificationCheck() {
        user?.reload()
        isVerified = user?.isEmailVerified == true
        if (isVerified) {
            showSetMasterPasswordDialog = true
            showVerificationWaiting = false
            infoMessage = ""
        } else {
            infoMessage = context.getString(R.string.die_e_mail_adresse_wurde_noch_nicht_best_tigt_bitte_berpr_fen_sie_ihren_posteingang)
        }
    }


    VerificationPoller(
        showVerificationWaiting = showVerificationWaiting,
        user = user,
        onVerified = {
            showSetMasterPasswordDialog = true
            showVerificationWaiting = false
            infoMessage = ""
        },
        onNotVerified = {
            infoMessage =
                context.getString(R.string.die_e_mail_adresse_wurde_noch_nicht_best_tigt_bitte_berpr_fen_sie_ihren_posteingang)
        }
    )

    if (showVerificationWaiting) {
        VerificationWaitingScreen(
            infoMessage = infoMessage,
            onCheckNow = { manualVerificationCheck() }
        )
    }
    LoginFormSection(
        isVisible = !showBiometricPrompt,
        isNewUser = isNewUser,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        error = error,
        infoMessage = infoMessage,
        loading = loading,
        biometricEnabled = biometricEnabled,
        hasPromptedBiometric = hasPromptedBiometric,
        emailFocusRequester = emailFocusRequester,
        passwordFocusRequester = passwordFocusRequester,
        focusManager = focusManager,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirmPassword = it },
        onToggleUserMode = { isNewUser = !isNewUser },
        onSubmit = {
            if (isNewUser) handleCreateAccount() else handleLogin()
        },
        onTriggerBiometric = {
            hasPromptedBiometric = true
            showBiometricPrompt = true
        },
        resetMessages = { resetMessages() },
        onGoogleSignInClick = {
            GoogleAuthManager.launchGoogleSignIn(
                context = context,
                launcher = googleSignInLauncher,
                isNewUser = isNewUser
            )
        },
        googleButtonEnabled = !loading, // or true if always enabled
        googleSignInLauncher = googleSignInLauncher

    )

    BiometricPromptWrapper(
        show = showBiometricPrompt,
        onSuccess = {
            showBiometricPrompt = false
            showMasterPasswordDialog = true
        },
        onError = {
            error = it
            showBiometricPrompt = false
        }
    )
    MasterPasswordDialog(
        show = showMasterPasswordDialog,
        onDismiss = {
            showMasterPasswordDialog = false
            masterPasswordInput = ""
        },
        masterPasswordInput = masterPasswordInput,
        onPasswordChange = { masterPasswordInput = it },
        context = context,
        navController = navController,
        onVerified = {
            showMasterPasswordDialog = false
        }
    )
    SetMasterPasswordDialog(
        show = showSetMasterPasswordDialog,
        onDismiss = {
            showSetMasterPasswordDialog = false
        },
        newPassword = newMasterPassword,
        confirmPassword = confirmMasterPassword,
        onNewPasswordChange = { newMasterPassword = it },
        onConfirmPasswordChange = { confirmMasterPassword = it },
        context = context,
        auth = auth,
        onSuccess = {
            showSetMasterPasswordDialog = false
            infoMessage = context.getString(R.string.die_angegebene_e_mail_adresse_wurde_erfolgreich_verifiziert)
        },
        onError = { error = it }
    )

    if (loading) {
        LoadingIndicator()
    }
    }
