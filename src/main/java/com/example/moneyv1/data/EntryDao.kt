// EntryDao.kt
package com.example.moneyv1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry)

    @Query("SELECT * FROM Entry")
    suspend fun getAllEntries(): List<Entry>
}
