package com.bbg.securevault.presentation.loginScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bbg.securevault.data.BiometricSettingsStore
import com.bbg.securevault.data.InactivityManager
import com.bbg.securevault.data.PasswordStore
import com.bbg.securevault.data.local.EncryptedPasswordDatabase
import com.bbg.securevault.data.local.repository.PasswordRepository
import com.bbg.securevault.presentation.passwords.BiometricAuthPrompt
import com.bbg.securevault.presentation.passwords.LoadingIndicator
import com.bbg.securevault.presentation.passwords.PasswordTextField
import com.bbg.securevault.presentation.passwords.masterPassword.SaveMasterPasswordToFirestore
import com.bbg.securevault.presentation.passwords.masterPassword.VerifyMasterPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Enoklit on 05.06.2025.
 */
@SuppressLint("ResourceAsColor")
@Composable
fun LoginScreen(navController: NavController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = FirebaseFirestore.getInstance()

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
    val context = LocalContext.current
    val biometricEnabled by BiometricSettingsStore.isEnabledFlow(context).collectAsState(initial = false)

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
                            showSetMasterPasswordDialog = true
                            isNewUser = false
                        } else {
                            error = "Account creation failed: ${task.exception?.localizedMessage}"
                        }
                    }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFF6C63FF),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("SecureVault", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Safe. Simple. Encrypted.", color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text(if (isNewUser) "Create Your Account" else "Login to Your Vault", fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            label = "Password",
            password = password,
            onPasswordChange = { password = it }
        )
        if (isNewUser) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                label = "Confirm Password",
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it }
            )
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = Color.Red, fontSize = 14.sp)
        }

        if (infoMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(infoMessage, color = Color(0xFF388E3C), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isNewUser) handleCreateAccount() else handleLogin()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(if (isNewUser) "Create Account" else "Login")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (isNewUser) "Already registered?" else "New here?", color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                resetMessages()
                isNewUser = !isNewUser
                email = ""
                password = ""
                confirmPassword = ""
            }) {
                Text(if (isNewUser) "Login" else "Create Account")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Your data is encrypted and stored securely.\nOnly verified users can access the vault.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 16.sp
        )
    }
    if (loading) {
        LoadingIndicator()
    }
    if (showMasterPasswordDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Security Check") },
            text = {
                Column {
                    Text("Enter your master password to proceed.")
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordTextField(
                        label = "Master Password",
                        password = masterPasswordInput,
                        onPasswordChange = { masterPasswordInput = it }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val user = auth.currentUser
                    if (user != null) {
                        VerifyMasterPassword(
                            uid = user.uid,
                            inputPassword = masterPasswordInput,
                            onSuccess = {
                                // ✅ Initialize repository inside PasswordStore
                                PasswordStore.setAuthenticated(
                                    context = context,
                                    authenticated = true,
                                    password = masterPasswordInput
                                )
                                // ✅ Load passwords immediately
                                CoroutineScope(Dispatchers.Main).launch {
                                    PasswordStore.loadFromDatabase()
                                }
                                // Optional: Update last activity for auto logout
                                InactivityManager.updateActivity()
                                // Continue with navigation flow
                                showMasterPasswordDialog = false
                                showBiometricPrompt = true
                            },
                            onError = {
                                error = it
                                masterPasswordInput = ""
                            }
                        )
                    }
                }) {
                    Text("Verify")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showMasterPasswordDialog = false
                    masterPasswordInput = ""
                }) {
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
    if (showSetMasterPasswordDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Set Master Password") },
            text = {
                Column {
                    Text("Create your secure Master Password.")
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordTextField(
                        label = "Master Password",
                        password = newMasterPassword,
                        onPasswordChange = { newMasterPassword = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PasswordTextField(
                        label = "Confirm Master Password",
                        password = confirmMasterPassword,
                        onPasswordChange = { confirmMasterPassword = it }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newMasterPassword == confirmMasterPassword && newMasterPassword.length >= 8) {
                        val user = auth.currentUser
                        if (user != null) {
                            SaveMasterPasswordToFirestore(
                                uid = user.uid,
                                password = newMasterPassword,
                                onSuccess = {
                                    showSetMasterPasswordDialog = false
                                    infoMessage = "Verification email sent. Please check your inbox."
                                },
                                onError = {
                                    error = it
                                }
                            )
                        }
                    } else {
                        error = "Passwords do not match or too short."
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSetMasterPasswordDialog = false
                }) {
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
    //only show BiometricAuthPrompt if both are true
    if (showBiometricPrompt) {
            if (biometricEnabled) {
                BiometricAuthPrompt(
                    onAuthSuccess = {
                        showBiometricPrompt = false
                        navController.navigate("tabs") {
                            popUpTo(0)
                        }
                    },
                    onAuthError = { message ->
                        error = message
                        showBiometricPrompt = false
                    }
                )
            } else {
                // If biometrics disabled, just navigate immediately
                showBiometricPrompt = false
                navController.navigate("tabs") {
                    popUpTo(0)
                }
            }
        }

    }
