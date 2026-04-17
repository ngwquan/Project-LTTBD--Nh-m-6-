package com.example.expensemanagement.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.sql.Date

@Entity(
    tableName = "saving_goals",
    foreignKeys = [
        ForeignKey (
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns =  ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey (
            entity = WalletEntity::class,
            parentColumns = ["id"],
            childColumns = ["wallet_id"]
        )
    ]
)

data class SavingGoalEntity (
    @PrimaryKey
    @ColumnInfo(name = "saving_goal_id")
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "wallet_id")
    val walletId: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Date,
    val status: String
)
