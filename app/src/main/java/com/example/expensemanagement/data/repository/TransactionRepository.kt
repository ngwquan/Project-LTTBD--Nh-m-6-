package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.TransactionDao
import com.example.expensemanagement.data.local.entity.TransactionEntity

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    suspend fun addTransaction(transaction: TransactionEntity) {
        transactionDao.insert(transaction)
    }

    suspend fun getTransactions(userId: Long): List<TransactionEntity> {
        return transactionDao.getByUser(userId)
    }

    suspend fun getTransactionsByWallet(walletId: Long): List<TransactionEntity> {
        return transactionDao.getByWallet(walletId)
    }

    suspend fun getTotalExpense(userId: Long): Double {
        return transactionDao.getTotalExpense(userId) ?: 0.0
    }

    suspend fun getTotalIncome(userId: Long): Double {
        return transactionDao.getTotalIncome(userId) ?: 0.0
    }

    suspend fun getBalance(userId: Long): Double {
        return transactionDao.getBalance(userId) ?: 0.0
    }

    suspend fun getExpensesByDateRange(userId: Long, startDate: Long, endDate: Long): List<TransactionEntity> {
        return transactionDao.getExpensesByDateRange(userId, startDate, endDate)
    }

    suspend fun getTotalExpenseByDateRange(userId: Long, startDate: Long, endDate: Long): Double {
        return transactionDao.getTotalExpenseByDateRange(userId, startDate, endDate) ?: 0.0
    }

    suspend fun getExpenseSumByCategory(userId: Long): List<com.example.expensemanagement.data.local.model.CategoryExpenseSum> {
        return transactionDao.getExpenseSumByCategory(userId)
    }

    suspend fun getHighestExpenseCategory(userId: Long): com.example.expensemanagement.data.local.model.CategoryExpenseSum? {
        return transactionDao.getHighestExpenseCategory(userId)
    }

    suspend fun getLargestExpenseTransaction(userId: Long): TransactionEntity? {
        return transactionDao.getLargestExpenseTransaction(userId)
    }

    suspend fun searchTransactions(userId: Long, keyword: String?, categoryId: Long? = null): List<TransactionEntity> {
        return transactionDao.searchTransactions(userId, keyword, categoryId)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }
}