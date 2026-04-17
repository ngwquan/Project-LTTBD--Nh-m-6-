package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.SavingGoalEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface SavingGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingGoalEntity)

    @Query("SELECT * FROM saving_goals WHERE user_id = :userId")
    suspend fun getByUser(userId: String): List<SavingGoalEntity>

    @Update
    suspend fun update(goal: SavingGoalEntity)

    @Delete
    suspend fun delete(goal: SavingGoalEntity)
}