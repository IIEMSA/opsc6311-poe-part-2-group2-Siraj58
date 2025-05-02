package com.example.moneyv1.data

import androidx.room.*
import com.example.moneyv1.model.Category
import com.example.moneyv1.model.TransactionEntry

@Dao
interface AppDao {
    // Category
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<Category>

    // Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(entry: TransactionEntry)

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    suspend fun getTransactionsByCategory(categoryId: Int): List<TransactionEntry>

    @Query("""
        SELECT * FROM transactions
        WHERE (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:isExpense IS NULL OR isExpense = :isExpense)
        AND (:startDate IS NULL OR timestamp >= :startDate)
        AND (:endDate IS NULL OR timestamp <= :endDate)
        ORDER BY timestamp DESC
    """)
    suspend fun filterTransactions(
        categoryId: Int?,
        isExpense: Boolean?,
        startDate: Long?,
        endDate: Long?
    ): List<TransactionEntry>
}
