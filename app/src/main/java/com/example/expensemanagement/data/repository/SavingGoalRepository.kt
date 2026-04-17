package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.SavingGoalDao
import com.example.expensemanagement.data.local.entity.SavingGoalEntity

class SavingGoalRepository(
    private val dao: SavingGoalDao
) {

    suspend fun addGoal(goal: SavingGoalEntity) {
        dao.insert(goal)
    }

    suspend fun getGoals(userId: String): List<SavingGoalEntity> {
        return dao.getByUser(userId)
    }

    suspend fun updateGoal(goal: SavingGoalEntity) {
        dao.update(goal)
    }

    suspend fun deleteGoal(goal: SavingGoalEntity) {
        dao.delete(goal)
    }
}