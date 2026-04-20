package com.example.expensemanagement.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Thiết lập giao diện chính
        setContentView(R.layout.activity_main)

        // 2. Ánh xạ các View từ XML
        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val btnNavExpense = findViewById<LinearLayout>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<LinearLayout>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<LinearLayout>(R.id.btnNavProfile)

        // 3. Hiển thị thông tin người dùng từ bộ nhớ tạm
        val userPrefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", "User")
        txtWelcome.text = "Xin chào, $username"

        // 4. XỬ LÝ SỰ KIỆN CLICK CHO THANH ĐIỀU HƯỚNG DƯỚI

        // --- Nút CHI TIÊU (Mở trang Lịch sử / Onboarding) ---
        btnNavExpense.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // --- Nút THỐNG KÊ ---
        btnNavStatistics.setOnClickListener {
            // Vì bạn đã xóa CategoryActivity, tôi chuyển hướng tạm thời sang HistoryActivity
            // để app không bị lỗi biên dịch.
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)

            // Thông báo để bạn biết trang này đang được thay thế
            Toast.makeText(this, "Tính năng Thống kê đang cập nhật", Toast.LENGTH_SHORT).show()
        }

        // --- Nút PROFILE (Mở trang Hồ sơ cá nhân) ---
        btnNavProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Thông báo chào mừng người dùng
        Toast.makeText(this, "Chào mừng $username quay trở lại!", Toast.LENGTH_SHORT).show()
    }
}