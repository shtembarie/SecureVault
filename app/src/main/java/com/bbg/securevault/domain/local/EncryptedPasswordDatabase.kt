package com.bbg.securevault.domain.local

import android.content.Context
import androidx.room.*
import com.bbg.securevault.domain.local.interfaces.PasswordDao
import com.bbg.securevault.data.models.PasswordEntry
import net.sqlcipher.database.SupportFactory
/**
 * Created by Enoklit on 12.06.2025.
 */
@Database(entities = [PasswordEntry::class], version = 1)
abstract class EncryptedPasswordDatabase : RoomDatabase() {
    abstract fun passwordDao(): PasswordDao

    companion object {
        @Volatile
        private var INSTANCE: EncryptedPasswordDatabase? = null

        fun getInstance(context: Context, masterPassword: String): EncryptedPasswordDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = net.sqlcipher.database.SQLiteDatabase.getBytes(masterPassword.toCharArray())
                val factory = SupportFactory(passphrase)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EncryptedPasswordDatabase::class.java,
                    "secure_vault.db"
                )
                    .fallbackToDestructiveMigration()
                    .openHelperFactory(factory)
                    .build()

                INSTANCE = instance
                instance
            }
        }
        @Synchronized
        fun resetInstance() {
            INSTANCE = null
        }

    }
}