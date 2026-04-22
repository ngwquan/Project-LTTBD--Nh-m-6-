package com.example.expensemanagement.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import com.example.expensemanagement.utils.MoneyUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Thiết lập giao diện chính
        setContentView(R.layout.activity_main)

        // 2. Ánh xạ các View từ XML
        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)

        val btnNavOverview = findViewById<android.view.View>(R.id.btnNavOverview)
        val btnNavExpense = findViewById<android.view.View>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<android.view.View>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<android.view.View>(R.id.btnNavProfile)
        val fabAdd = findViewById<android.view.View>(R.id.fab_add)

        // 3. Hiển thị thông tin người dùng từ bộ nhớ tạm
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        val userId = globalPref.getLong("current_user_id", -1)
        if (userId == -1L) {
            startActivity(Intent(this, com.example.expensemanagement.ui.auth.LoginActivity::class.java))
            finish()
            return
        }

        val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", null) ?: "User"

        txtWelcome.text = getString(R.string.welcome_user, username)
        val txtMoney = findViewById<TextView>(R.id.txtMoney)

        val money = userPrefs.getString("money", null) ?: "0"
        val currency = userPrefs.getString("currency", null) ?: "₫"

        val formattedMoney = MoneyUtils.format(money, currency)
        txtMoney.text = formattedMoney

        // 4. XỬ LÝ SỰ KIỆN CLICK CHO THANH ĐIỀU HƯỚNG DƯỚI
        // TONG QUAN
        btnNavOverview?.setOnClickListener {
        }
        // CHI TIEU
        btnNavExpense?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        // THONG KE
        btnNavStatistics?.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        // PROFILE
        btnNavProfile?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        // FAB ADD
        fabAdd?.setOnClickListener {
            val intent = Intent(this, com.example.expensemanagement.ui.main.AddExpenseActivity::class.java)
            startActivity(intent)
        }

        // Thông báo chào mừng người dùng
        Toast.makeText(this, getString(R.string.welcome_back, username), Toast.LENGTH_SHORT).show()
    }
}