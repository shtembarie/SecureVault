package com.bbg.securevault.presentation.passwords.masterPassword

import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

/**
 * Created by Enoklit on 11.06.2025.
 */
fun SaveMasterPasswordToFirestore(
    uid: String,
    password: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val salt = generateSalt()
    val hashed = hashPasswordPBKDF2(password, salt)
    val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)

    val data = hashMapOf(
        "secondPasswordHash" to hashed,
        "secondPasswordSalt" to saltBase64
    )

    firestore.collection("users").document(uid)
        .set(data, SetOptions.merge())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onError("Saving failed: ${e.message}") }
}