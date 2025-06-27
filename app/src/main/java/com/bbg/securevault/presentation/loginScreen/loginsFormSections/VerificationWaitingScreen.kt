package com.bbg.securevault.presentation.loginScreen.loginsFormSections

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bbg.securevault.R


/**
 * Created by Enoklit on 27.06.2025.
 */
@Composable
fun VerificationWaitingScreen(
    infoMessage: String,
    onCheckNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.bitte_best_tigen_sie_ihre_e_mail_adresse_wir_berpr_fen_ihren_best_tigungsstatus_alle_1_sekunde),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCheckNow) {
            Text(stringResource(R.string.berpr_fen_sie_jetzt_die_verifizierung))
        }
        if (infoMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(infoMessage, color = Color.Gray)
        }
    }
}
