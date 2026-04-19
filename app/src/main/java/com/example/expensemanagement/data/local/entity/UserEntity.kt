package com.example.expensemanagement.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val id: Long = 0,
    val fullName: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val currency: String = "VND",
    val createdAt: Long = System.currentTimeMillis()
)