package com.example.expensemanagement.data.local.dao

import androidx.room.*
import com.example.expensemanagement.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // Dòng này sẽ sửa lỗi "Unresolved reference" cho bạn
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)
}