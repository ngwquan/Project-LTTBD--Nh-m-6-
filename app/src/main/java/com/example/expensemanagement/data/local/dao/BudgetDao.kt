package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.BudgetEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE user_id = :userId")
    suspend fun getByUser(userId: String): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE category_id = :categoryId")
    suspend fun getByCategory(categoryId: String): List<BudgetEntity>

    @Delete
    suspend fun delete(budget: BudgetEntity)
}