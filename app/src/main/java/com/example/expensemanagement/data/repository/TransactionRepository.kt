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

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }
}