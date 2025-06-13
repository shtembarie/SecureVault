package com.bbg.securevault.data.local.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bbg.securevault.domain.models.PasswordEntry

/**
 * Created by Enoklit on 13.06.2025.
 */
@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords")
    suspend fun getAll(): List<PasswordEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: PasswordEntry)

    @Delete
    suspend fun delete(entry: PasswordEntry)

    @Query("DELETE FROM passwords")
    suspend fun clearAll()

    @Query("SELECT * FROM passwords WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): PasswordEntry?

}
