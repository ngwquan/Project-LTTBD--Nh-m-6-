package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.BudgetDao
import com.example.expensemanagement.data.local.entity.BudgetEntity

class BudgetRepository(
    private val budgetDao: BudgetDao
) {

    suspend fun addBudget(budget: BudgetEntity) {
        budgetDao.insert(budget)
    }

    suspend fun getBudgets(userId: Long): List<BudgetEntity> {
        return budgetDao.getByUser(userId)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.delete(budget)
    }
}