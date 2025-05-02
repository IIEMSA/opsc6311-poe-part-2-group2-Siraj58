package com.example.moneyv1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val title: String,
    val cost: Double,
    val isExpense: Boolean,
    val timestamp: Long,
    val imageUri: String? = null
)
