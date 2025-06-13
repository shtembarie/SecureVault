package com.bbg.securevault.presentation.passwords.masterPassword

import androidx.compose.runtime.Composable
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.lang.Error

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
    val hashed = hashPassword(password)
    val data = hashMapOf("secondPasswordHash" to hashed)

    firestore.collection("users").document(uid)
        .set(data, SetOptions.merge())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener {e -> onError("Saving failed: ${e.message}")}
}