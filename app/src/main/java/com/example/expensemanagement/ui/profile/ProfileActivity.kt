package com.example.expensemanagement.ui.profile

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
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
    }
}