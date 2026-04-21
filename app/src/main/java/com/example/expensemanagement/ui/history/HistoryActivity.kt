package com.example.expensemanagement.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.TextView // Thêm nếu bạn muốn bắt sự kiện nút
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.ui.profile.ProfileActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Kết nối với file giao diện Onboarding (phiên bản 4 bước hướng dẫn)
        setContentView(R.layout.activity_onboarding)

        // 2. (Tùy chọn) Bắt sự kiện click cho các nút "Thêm", "Tạo" từ ảnh 2
        val btnViewDemo = findViewById<TextView>(R.id.btnViewDemoData)

        btnViewDemo?.setOnClickListener {
            Toast.makeText(this, "Tính năng xem dữ liệu mẫu đang phát triển!", Toast.LENGTH_SHORT).show()
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

        // 3. Xử lý nút quay lại (nếu bạn có nút mũi tên quay lại)
        // val btnBack = findViewById<ImageView>(R.id.btnBack)
        // btnBack?.setOnClickListener { finish() }
    }
}