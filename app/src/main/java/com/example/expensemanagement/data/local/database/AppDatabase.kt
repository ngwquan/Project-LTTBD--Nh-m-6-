package com.example.expensemanagement.data.local.database
import com.example.expensemanagement.data.local.entity.UserEntity
import com.example.expensemanagement.data.local.entity.WalletEntity
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.data.local.entity.BudgetEntity
import com.example.expensemanagement.data.local.entity.RecurringTransactionEntity
import com.example.expensemanagement.data.local.entity.SavingGoalEntity
import com.example.expensemanagement.data.local.dao.UserDao
import com.example.expensemanagement.data.local.dao.WalletDao
import com.example.expensemanagement.data.local.dao.CategoryDao
import com.example.expensemanagement.data.local.dao.TransactionDao
import com.example.expensemanagement.data.local.dao.BudgetDao
import com.example.expensemanagement.data.local.dao.RecurringTransactionDao
import com.example.expensemanagement.data.local.dao.SavingGoalDao
import androidx.room.Room
import androidx.room.Database
import android.content.Context
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        WalletEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class,
        RecurringTransactionEntity::class,
        SavingGoalEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun recurringDao(): RecurringTransactionDao
    abstract fun goalDao(): SavingGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}