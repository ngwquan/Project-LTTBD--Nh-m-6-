package com.example.expensemanagement.data.local.dao

import androidx.room.*
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.data.local.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    // --- HÀM BỔ SUNG ĐỂ HIỂN THỊ TÊN DANH MỤC ĐỘNG ---
    @Query("""
        SELECT 
            t.transaction_id AS id, 
            t.amount AS amount, 
            t.note AS note, 
            t.transactionDate AS date, 
            t.type AS type, 
            c.name AS categoryName,
            '#E0E0E0' AS categoryColor 
        FROM transactions t 
        INNER JOIN categories c ON t.category_id = c.category_id 
        WHERE t.user_id = :userId 
        ORDER BY t.transactionDate DESC
    """)
    fun getAllTransactionsWithCategory(userId: Long): Flow<List<TransactionWithCategory>>
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY transactionDate DESC")
    suspend fun getByUser(userId: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE transaction_id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE wallet_id = :walletId")
    suspend fun getByWallet(walletId: Long): List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE'")
    suspend fun getTotalExpense(userId: Long): Double?

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}