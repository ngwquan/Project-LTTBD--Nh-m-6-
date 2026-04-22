package com.example.expensemanagement.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.auth.LoginActivity
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import com.example.expensemanagement.utils.MoneyUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Kiểm tra đăng nhập TRƯỚC KHI setContentView để tránh treo UI
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -99L)

        if (userId == -99L) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // 2. Ánh xạ View
        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val tvTotalExpense = findViewById<TextView>(R.id.tvTotalExpense)
        val tvTotalIncome = findViewById<TextView>(R.id.tvTotalIncome)

        val btnNavExpense = findViewById<View>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<View>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<View>(R.id.btnNavProfile)
        val fabAdd = findViewById<View>(R.id.fab_add)

        // 3. Hiển thị thông tin
        var username = globalPref.getString("username", "Người dùng")
        if (userId == -1L) {
            username = "Quản trị viên (Admin)"
        }
        val currency = globalPref.getString("currency", "VND") ?: "VND"

        txtWelcome.text = "Xin chào, $username"
        tvTotalExpense.text = MoneyUtils.format("0", currency)
        tvTotalIncome.text = MoneyUtils.format("0", currency)

        // 4. Điều hướng
        btnNavExpense?.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java).apply { 
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) 
            })
        }
        btnNavStatistics?.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java).apply { 
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) 
            })
        }
        btnNavProfile?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply { 
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) 
            })
        }
        fabAdd?.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }
}
