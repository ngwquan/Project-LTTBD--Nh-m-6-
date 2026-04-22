package com.example.expensemanagement.utils

import java.text.NumberFormat
import java.util.Locale

object MoneyUtils {
    fun format(amount: String, currency: String): String {
        val number = amount.toLongOrNull() ?: 0
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        return format.format(number) + " " + currency
    }
}