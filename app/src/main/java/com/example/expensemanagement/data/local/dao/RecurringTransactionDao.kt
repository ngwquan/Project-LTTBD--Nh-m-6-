package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.RecurringTransactionEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface RecurringTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurring: RecurringTransactionEntity)

    @Query("SELECT * FROM recurring_transactions WHERE user_id = :userId")
    suspend fun getByUser(userId: Long): List<RecurringTransactionEntity>

    @Update
    suspend fun update(recurring: RecurringTransactionEntity)
}