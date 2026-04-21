package com.example.expensemanagement.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Kết nối với file giao diện profile (Nền trắng, nút xanh)
        setContentView(R.layout.activity_profile)

        // 2. Ánh xạ các thành phần giao diện
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)

        // 3. Lấy tên người dùng từ SharedPreferences để hiển thị
        val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val username = userPrefs.getString("username", "Người dùng")
        tvUsername.text = username

        // 4. Xử lý sự kiện nút Chỉnh sửa hồ sơ
        btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Tính năng chỉnh sửa đang được phát triển!", Toast.LENGTH_SHORT).show()
        }

        val btnNavOverview = findViewById<android.view.View>(R.id.btnNavOverview)
        val btnNavExpense = findViewById<android.view.View>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<android.view.View>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<android.view.View>(R.id.btnNavProfile)
        val fabAdd = findViewById<android.view.View>(R.id.fab_add)

        // TONG QUAN
        btnNavOverview?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
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
        }
        // FAB ADD
        fabAdd?.setOnClickListener {
            val intent = Intent(this, com.example.expensemanagement.ui.main.AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}