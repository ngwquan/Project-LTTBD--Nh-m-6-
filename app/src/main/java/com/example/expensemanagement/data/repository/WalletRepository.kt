package com.example.expensemanagement.data.repository
import com.example.expensemanagement.data.local.dao.WalletDao
import com.example.expensemanagement.data.local.entity.WalletEntity

class WalletRepository(
    private val walletDao: WalletDao
) {

    suspend fun addWallet(wallet: WalletEntity) {
        walletDao.insert(wallet)
    }

    suspend fun getWallets(userId: Long): List<WalletEntity> {
        return walletDao.getByUser(userId)
    }

    suspend fun updateWallet(wallet: WalletEntity) {
        walletDao.update(wallet)
    }

    suspend fun deleteWallet(wallet: WalletEntity) {
        walletDao.delete(wallet)
    }
}