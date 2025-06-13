package com.bbg.securevault.presentation.passwords.masterPassword

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Created by Enoklit on 11.06.2025.
 */

fun VerifyMasterPassword(uid: String, inputPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit){
    val firestore = FirebaseFirestore.getInstance()
    val inputHash = hashPassword(inputPassword)

    firestore.collection("users").document(uid).get()
        .addOnSuccessListener { doc ->
            val storedHash = doc.getString("secondPasswordHash")
            if (storedHash == inputHash) {
                onSuccess()
            }else{
                onError("Incorrect master password")
            }
        }
        .addOnFailureListener { e ->
            onError("Verification failed: ${e.message}")
        }
}