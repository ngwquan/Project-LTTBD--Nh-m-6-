package com.example.expensemanagement.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo


@Entity(
    tableName = "wallets",
    foreignKeys = [
        ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["user_id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "wallet_id")
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    val name: String,
    val type: String,
    val balance: Double,
    val isDefault: Boolean = true
)