package com.example.expensemanagement.data.local.model

import androidx.room.ColumnInfo

data class CategoryExpenseSum(
    @ColumnInfo(name = "categoryName")
    val categoryName: String,
    @ColumnInfo(name = "totalAmount")
    val totalAmount: Double
)
