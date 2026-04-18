package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.CategoryDao
import com.example.expensemanagement.data.local.entity.CategoryEntity

class CategoryRepository(
    private val categoryDao: CategoryDao
) {

    suspend fun addCategory(category: CategoryEntity) {
        categoryDao.insert(category)
    }

    suspend fun getCategories(userId: Long): List<CategoryEntity> {
        return categoryDao.getByUser(userId)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.delete(category)
    }
}