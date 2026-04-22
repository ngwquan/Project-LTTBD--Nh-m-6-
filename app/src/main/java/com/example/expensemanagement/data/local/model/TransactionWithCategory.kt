package com.example.expensemanagement.data.local.model

/**
 * Class này dùng để chứa dữ liệu giao dịch đã được kết hợp (JOIN) với danh mục.
 * Giúp hiển thị tên "Ăn uống" và màu sắc tương ứng lên màn hình Lịch sử.
 */
data class TransactionWithCategory(
    val id: Long,
    val amount: Double,
    val note: String,
    val date: Long,
    val type: String,          // "EXPENSE" hoặc "INCOME"
    val categoryName: String,  // Ví dụ: "Ăn uống", "Mua sắm"
    val categoryColor: String  // Ví dụ: "#F9E2B0"
)