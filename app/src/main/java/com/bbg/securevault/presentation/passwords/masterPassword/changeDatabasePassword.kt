package com.bbg.securevault.presentation.passwords.masterPassword

import android.content.Context

/**
 * Created by Enoklit on 25.06.2025.
 */

fun changeDatabasePassword(
    context: Context,
    oldPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    try {
        val dbFile = context.getDatabasePath("secure_vault.db")
        val oldDb = net.sqlcipher.database.SQLiteDatabase.openDatabase(
            dbFile.absolutePath,
            oldPassword,
            null,
            net.sqlcipher.database.SQLiteDatabase.OPEN_READWRITE
        )

        oldDb.changePassword(newPassword)
        oldDb.close()
        onSuccess()
    } catch (e: Exception) {
        onError("Database password change failed: ${e.message}")
    }
}
