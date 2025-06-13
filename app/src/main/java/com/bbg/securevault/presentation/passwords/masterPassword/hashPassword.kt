package com.bbg.securevault.presentation.passwords.masterPassword

import androidx.compose.runtime.Composable
import java.security.MessageDigest

/**
 * Created by Enoklit on 11.06.2025.
 */


fun hashPassword(password: String): String{
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(password.toByteArray())
    return hashBytes.joinToString(separator = ""){byte -> "%02x".format(byte)}
}