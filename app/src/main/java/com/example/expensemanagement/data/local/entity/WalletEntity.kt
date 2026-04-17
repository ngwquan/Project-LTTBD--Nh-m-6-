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
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WalletEntity(
    @PrimaryKey
    @ColumnInfo(name = "wallet_id")
    val id: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    val name: String,
    val type: String,
    val balance: Double,
    val isDefault: Boolean
)