package com.example.expensemanagement.data.local.dao
import com.example.expensemanagement.data.local.entity.WalletEntity
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface WalletDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: WalletEntity)

    @Query("SELECT * FROM wallets WHERE user_id = :userId")
    suspend fun getByUser(userId: String): List<WalletEntity>

    @Query("SELECT * FROM wallets WHERE wallet_id = :id")
    suspend fun getById(id: String): WalletEntity?

    @Update
    suspend fun update(wallet: WalletEntity)

    @Delete
    suspend fun delete(wallet: WalletEntity)
}