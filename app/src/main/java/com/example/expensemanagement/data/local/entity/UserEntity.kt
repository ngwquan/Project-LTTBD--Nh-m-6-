package com.example.expensemanagement.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val currency: String,
    val createdAt: Date
)