package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.CategoryEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE user_id = :userId")
    suspend fun getByUser(userId: String): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE category_id = :id")
    suspend fun getById(id: String): CategoryEntity?

    @Delete
    suspend fun delete(category: CategoryEntity)
}