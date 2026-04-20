package com.example.expensemanagement.ui.history

import android.os.Bundle
import android.widget.TextView // Thêm nếu bạn muốn bắt sự kiện nút
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R

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

        // 3. Xử lý nút quay lại (nếu bạn có nút mũi tên quay lại)
        // val btnBack = findViewById<ImageView>(R.id.btnBack)
        // btnBack?.setOnClickListener { finish() }
    }
}