package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.TransactionEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("""
        SELECT * FROM transactions 
        WHERE user_id = :userId 
        ORDER BY transactionDate DESC
    """)
    suspend fun getByUser(userId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE wallet_id = :walletId")
    suspend fun getByWallet(walletId: String): List<TransactionEntity>

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE user_id = :userId AND type = 'EXPENSE'
    """)
    suspend fun getTotalExpense(userId: String): Double?

    @Delete
    suspend fun delete(transaction: TransactionEntity)
}