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
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.ui.auth.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvCurrency = findViewById<TextView>(R.id.tvCurrency)
        val btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)
        val btnLogout = findViewById<View>(R.id.btnLogout)

        // 1. Lấy userId hiện tại từ SharedPreferences
        val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = userPrefs.getLong("current_user_id", -1L)
        val db = AppDatabase.getDatabase(this)

        // 2. Hiển thị thông tin
        lifecycleScope.launch {
            db.userDao().getByIdFlow(userId).collect { user ->
                if (user != null) {
                    tvUsername.text = user.fullName
                    tvEmail.text = user.email
                    tvCurrency.text = user.currency

                    btnEditProfile.setOnClickListener {
                        val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
                        startActivity(intent)
                    }
                } else if (userId == -1L) {
                    tvUsername.text = "Administrator"
                    tvEmail.text = "admin@system.com"
                    tvCurrency.text = "All"
                    btnEditProfile.visibility = View.GONE
                }
            }
        }

        // Xử lý Đăng xuất
        btnLogout.setOnClickListener {
            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            prefs.edit().clear().apply()

            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        setupBottomNavigation()
    }

    private fun showUserInfoDialog(name: String, username: String, email: String, currency: String) {
        val message = """
            Họ tên: $name
            Tên đăng nhập: $username
            Email: $email
            Đơn vị tiền tệ: $currency
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Thông tin cá nhân")
            .setMessage(message)
            .setPositiveButton("Đóng") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun setupBottomNavigation() {
        val btnNavOverview = findViewById<View>(R.id.btnNavOverview)
        val btnNavExpense = findViewById<View>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<View>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<View>(R.id.btnNavProfile)
        val fabAdd = findViewById<View>(R.id.fab_add)

        btnNavOverview?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        btnNavExpense?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        btnNavStatistics?.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
        btnNavProfile?.setOnClickListener {
            // Đang ở Profile rồi
        }
        fabAdd?.setOnClickListener {
            val intent = Intent(this, com.example.expensemanagement.ui.main.AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}
