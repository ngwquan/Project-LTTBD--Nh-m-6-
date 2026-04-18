package com.example.expensemanagement.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "recurring_transactions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WalletEntity::class,
            parentColumns = ["wallet_id"],
            childColumns = ["wallet_id"]
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"]
        )
    ]
)

data class RecurringTransactionEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recurring_transaction_id")
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "wallet_id")
    val walletId: Long,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    val amount: Double,
    val type: String,
    val note: String,
    val frequency: String,
    val nextDate: Long,
    val isActive: Boolean
)
