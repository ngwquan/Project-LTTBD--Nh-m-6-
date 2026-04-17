package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.RecurringTransactionDao
import com.example.expensemanagement.data.local.entity.RecurringTransactionEntity

class RecurringTransactionRepository(
    private val dao: RecurringTransactionDao
) {

    suspend fun addRecurring(recurring: RecurringTransactionEntity) {
        dao.insert(recurring)
    }

    suspend fun getRecurring(userId: String): List<RecurringTransactionEntity> {
        return dao.getByUser(userId)
    }

    suspend fun updateRecurring(recurring: RecurringTransactionEntity) {
        dao.update(recurring)
    }
}