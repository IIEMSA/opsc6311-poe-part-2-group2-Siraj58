package com.example.moneyv1.data

import androidx.room.Dao
import androidx.room.Query
import com.example.moneyv1.model.TransactionEntry

@Dao
interface EntryDao {

    @Query("SELECT * FROM transactions")
    suspend fun getAllEntries(): List<TransactionEntry>

    // Add any other needed queries here
}
