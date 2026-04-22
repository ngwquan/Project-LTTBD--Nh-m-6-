package com.example.expensemanagement.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.ui.main.AddExpenseActivity
import com.example.expensemanagement.ui.auth.LoginActivity
import com.example.expensemanagement.utils.MoneyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupUI()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun setupUI() {
        findViewById<android.view.View>(R.id.btnEditProfile).setOnClickListener {
            Toast.makeText(this, "Tính năng chỉnh sửa đang phát triển", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.btnManageCategories).setOnClickListener {
            Toast.makeText(this, "Tính năng quản lý danh mục", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.btnLogout).setOnClickListener {
            logout()
        }
    }

    private fun loadUserData() {
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvUserBalance = findViewById<TextView>(R.id.tvUserBalance)
        
        // Sử dụng cùng logic với MainActivity để lấy userId
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)
        
        if (userId == -1L) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val usernamePref = userPrefs.getString("username", "User")
        val currency = userPrefs.getString("currency", "₫") ?: "₫"
        
        tvUsername.text = usernamePref

        // Tính toán số dư từ Database để đảm bảo chính xác tuyệt đối
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@ProfileActivity)
            val user = db.userDao().getById(userId)
            val transactions = db.transactionDao().getByUser(userId)
            
            val displayName = user?.fullName ?: usernamePref
            
            var totalExp = 0.0
            var totalInc = 0.0
            
            for (t in transactions) {
                if (t.type == "EXPENSE") totalExp += t.amount
                else totalInc += t.amount
            }
            
            val balance = totalInc - totalExp

            withContext(Dispatchers.Main) {
                // Sử dụng MoneyUtils để định dạng tiền giống trang Main
                tvUsername.text = displayName
                tvUserBalance.text = "Số dư: ${MoneyUtils.format(balance.toLong().toString(), currency)}"
                
                // Cập nhật lại SharedPreferences để đồng bộ
                userPrefs.edit()
                    .putString("money", balance.toLong().toString())
                    .putString("username", displayName)
                    .apply()
            }
        }
    }

    private fun logout() {
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        globalPref.edit().remove("current_user_id").apply()
        
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupNavigation() {
        findViewById<android.view.View>(R.id.btnNavOverview)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }

        findViewById<android.view.View>(R.id.btnNavHistory)?.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }

        findViewById<android.view.View>(R.id.btnNavCategories)?.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
            overridePendingTransition(0, 0)
        }

        findViewById<android.view.View>(R.id.btnNavStatistics)?.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
    }
}
