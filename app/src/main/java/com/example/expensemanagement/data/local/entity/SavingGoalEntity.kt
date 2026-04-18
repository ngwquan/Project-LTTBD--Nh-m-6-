package com.example.expensemanagement.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "saving_goals",
    foreignKeys = [
        ForeignKey (
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns =  ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey (
            entity = WalletEntity::class,
            parentColumns = ["wallet_id"],
            childColumns = ["wallet_id"]
        )
    ]
)

data class SavingGoalEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "saving_goal_id")
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "wallet_id")
    val walletId: Long,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Long,
    val status: String
)
