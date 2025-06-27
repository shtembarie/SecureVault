package com.bbg.securevault.presentation.passwords.masterPassword


import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


/**
 * Created by Enoklit on 11.06.2025.
 */

fun generateSalt(): ByteArray {
    val salt = ByteArray(16)
    SecureRandom().nextBytes(salt)
    return salt
}
fun hashPasswordPBKDF2(
    password: String,
    salt: ByteArray,
    iterations: Int = 100_000,
    keyLength: Int = 256
): String {
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    val hash = factory.generateSecret(spec).encoded
    return Base64.encodeToString(hash, Base64.NO_WRAP)
}
fun HasMasterPassword(
    uid: String,
    onResult: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(uid).get()
        .addOnSuccessListener { doc ->
            val hasPassword = doc.contains("secondPasswordHash")
            onResult(hasPassword)
        }
        .addOnFailureListener { e ->
            onError("Check failed: ${e.message}")
        }
}
