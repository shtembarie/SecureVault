package com.bbg.securevault.presentation.loginScreen.loginsFormSections.biometrics

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbg.securevault.R
import com.bbg.securevault.domain.googlesigninandup.GoogleAuthManager
import com.bbg.securevault.presentation.passwords.PasswordTextField

/**
 * Created by Enoklit on 27.06.2025.
 */

@Composable
fun LoginFormSection(
    isVisible: Boolean,
    isNewUser: Boolean,
    email: String,
    password: String,
    confirmPassword: String,
    error: String,
    infoMessage: String,
    loading: Boolean,
    biometricEnabled: Boolean,
    hasPromptedBiometric: Boolean,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    focusManager: FocusManager,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleUserMode: () -> Unit,
    onSubmit: () -> Unit,
    onTriggerBiometric: () -> Unit,
    resetMessages: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    googleButtonEnabled: Boolean,
    googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>

) {
    if (!isVisible) return
    val context = LocalContext.current
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
        Text(stringResource(R.string.secure_vault), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.sicher_einfach_verschl_sselt), color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            if (isNewUser) stringResource(R.string.erstellen_sie_ihr_konto) else stringResource(R.string.melden_sie_sich_mit_ihre_konto_an),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.e_mail_adresse)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester)
                .onFocusChanged { focusState ->
                    if (
                        focusState.isFocused &&
                        biometricEnabled &&
                        !hasPromptedBiometric
                    ) {
                        focusManager.clearFocus()
                        onTriggerBiometric()
                    }
                },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            label = stringResource(R.string.passwort),
            password = password,
            onPasswordChange = onPasswordChange,
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
                .onFocusChanged { focusState ->
                    if (
                        focusState.isFocused &&
                        biometricEnabled &&
                        !hasPromptedBiometric
                    ) {
                        focusManager.clearFocus()
                        onTriggerBiometric()
                    }
                }
        )

        if (isNewUser) {
            Spacer(modifier = Modifier.height(8.dp))
            PasswordTextField(
                label = stringResource(R.string.passwort_best_tigen),
                password = confirmPassword,
                onPasswordChange = onConfirmPasswordChange
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
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(6.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            enabled = !loading,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gmail_neu),
                    contentDescription = "Gmail logo",
                    modifier = Modifier
                        .size(34.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = if (isNewUser) stringResource(R.string.konto_erstellen) else stringResource(R.string.anmelden),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (false) { // oder: if (shouldShowGoogleButton)
            Button(
                onClick = {
                    GoogleAuthManager.launchGoogleSignIn(
                        context = context,
                        launcher = googleSignInLauncher,
                        isNewUser = isNewUser
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(6.dp)),
                enabled = googleButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "Google logo",
                        modifier = Modifier
                            .size(34.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = if (isNewUser)
                            stringResource(R.string.registrieren_mit_google)
                        else
                            stringResource(R.string.anmelden_mit_google),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }



        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (isNewUser) stringResource(R.string.bereits_registriert) else stringResource(R.string.neu_hier), color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                resetMessages()
                onToggleUserMode()
            }) {
                Text(if (isNewUser) stringResource(R.string.anmelden) else stringResource(R.string.konto_erstellen))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            stringResource(R.string.ihre_daten_sind_verschl_sselt_und_sicher_gespeichert_nnur_verifizierte_benutzer_k_nnen_auf_den_tresor_zugreifen),
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 16.sp
        )
    }
}