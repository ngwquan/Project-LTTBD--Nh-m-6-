package com.example.expensemanagement.data.local.dao

import androidx.room.*
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.data.local.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    // --- HÀM BỔ SUNG ĐỂ HIỂN THỊ TÊN DANH MỤC ĐỘNG ---
    @Query("""
        SELECT 
            t.transaction_id AS id, 
            t.amount AS amount, 
            t.note AS note, 
            t.transactionDate AS date, 
            t.type AS type, 
            c.name AS categoryName,
            '#E0E0E0' AS categoryColor 
        FROM transactions t 
        INNER JOIN categories c ON t.category_id = c.category_id 
        WHERE t.user_id = :userId 
        ORDER BY t.transactionDate DESC
    """)
    fun getAllTransactionsWithCategory(userId: Long): Flow<List<TransactionWithCategory>>
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY transactionDate DESC")
    suspend fun getByUser(userId: Long): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE transaction_id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE wallet_id = :walletId")
    suspend fun getByWallet(walletId: Long): List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE'")
    suspend fun getTotalExpense(userId: Long): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'INCOME'")
    suspend fun getTotalIncome(userId: Long): Double?

    @Query("SELECT (SELECT IFNULL(SUM(amount), 0) FROM transactions WHERE user_id = :userId AND type = 'INCOME') - (SELECT IFNULL(SUM(amount), 0) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE')")
    suspend fun getBalance(userId: Long): Double?

    // --- LẤY DỮ LIỆU CHI TIÊU THEO KHOẢNG THỜI GIAN (Bao gồm theo tháng) ---
    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate ORDER BY transactionDate DESC")
    suspend fun getExpensesByDateRange(userId: Long, startDate: Long, endDate: Long): List<TransactionEntity>

    @Query("SELECT SUM(amount) FROM transactions WHERE user_id = :userId AND type = 'EXPENSE' AND transactionDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalExpenseByDateRange(userId: Long, startDate: Long, endDate: Long): Double?

    // --- LẤY TỔNG CHI TIÊU THEO TỪNG DANH MỤC ---
    @Query("""
        SELECT c.name AS categoryName, SUM(t.amount) AS totalAmount 
        FROM transactions t 
        INNER JOIN categories c ON t.category_id = c.category_id 
        WHERE t.user_id = :userId AND t.type = 'EXPENSE'
        GROUP BY c.category_id, c.name
        ORDER BY totalAmount DESC
    """)
    suspend fun getExpenseSumByCategory(userId: Long): List<com.example.expensemanagement.data.local.model.CategoryExpenseSum>

    // --- LẤY DANH MỤC CHI NHIỀU NHẤT ---
    @Query("""
        SELECT c.name AS categoryName, SUM(t.amount) AS totalAmount 
        FROM transactions t 
        INNER JOIN categories c ON t.category_id = c.category_id 
        WHERE t.user_id = :userId AND t.type = 'EXPENSE'
        GROUP BY c.category_id, c.name
        ORDER BY totalAmount DESC
        LIMIT 1
    """)
    suspend fun getHighestExpenseCategory(userId: Long): com.example.expensemanagement.data.local.model.CategoryExpenseSum?

    // --- LẤY GIAO DỊCH CHI TIÊU LỚN NHẤT ---
    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = 'EXPENSE' ORDER BY amount DESC LIMIT 1")
    suspend fun getLargestExpenseTransaction(userId: Long): TransactionEntity?

    // --- TÌM KIẾM THEO TỪ KHÓA TRONG GHI CHÚ VÀ LỌC THEO CATEGORY ---
    @Query("""
        SELECT * FROM transactions 
        WHERE user_id = :userId 
        AND (:categoryId IS NULL OR category_id = :categoryId)
        AND (:keyword IS NULL OR note LIKE '%' || :keyword || '%')
        ORDER BY transactionDate DESC
    """)
    suspend fun searchTransactions(userId: Long, keyword: String?, categoryId: Long?): List<TransactionEntity>

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE transaction_id = :id")
    suspend fun deleteById(id: Long)
}