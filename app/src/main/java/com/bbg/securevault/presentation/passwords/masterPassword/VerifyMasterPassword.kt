package com.bbg.securevault.presentation.passwords.masterPassword

import android.util.Base64
import com.bbg.securevault.R
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by Enoklit on 11.06.2025.
 */
fun VerifyMasterPassword(
    uid: String,
    inputPassword: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()

    firestore.collection("users").document(uid).get()
        .addOnSuccessListener { doc ->
            val storedHash = doc.getString("secondPasswordHash")
            val storedSaltBase64 = doc.getString("secondPasswordSalt")

            if (storedHash == null || storedSaltBase64 == null) {
                onError("No password data found")
                return@addOnSuccessListener
            }

            val salt = Base64.decode(storedSaltBase64, Base64.DEFAULT)
            val inputHash = hashPasswordPBKDF2(inputPassword, salt)

            if (inputHash == storedHash) {
                onSuccess()
            } else {
                onError("Incorrect master password")
            }
        }
        .addOnFailureListener { e ->
            onError("Verification failed: ${e.message}")
        }
}