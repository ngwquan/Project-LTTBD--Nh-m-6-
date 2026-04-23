package com.example.expensemanagement.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import com.example.expensemanagement.utils.MoneyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var txtMoney: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var txtWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtWelcome = findViewById(R.id.txtWelcome)
        txtMoney = findViewById(R.id.txtMoney)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun setupNavigation() {
        findViewById<android.view.View>(R.id.btnNavCategories)?.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        findViewById<android.view.View>(R.id.btnNavHistory)?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        findViewById<android.view.View>(R.id.tvViewAll)?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        findViewById<android.view.View>(R.id.btnNavStatistics)?.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }

        findViewById<android.view.View>(R.id.btnNavProfile)?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun refreshData() {
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)
        if (userId == -1L) {
            startActivity(Intent(this, com.example.expensemanagement.ui.auth.LoginActivity::class.java))
            finish()
            return
        }

        val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val usernamePref = userPrefs.getString("username", "User")
        val currency = userPrefs.getString("currency", "₫") ?: "₫"

        val amount_show = userPrefs.getLong("initial_balance", 0L)
        
        txtWelcome.text = "Xin chào, $usernamePref"

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@MainActivity)
            val user = db.userDao().getById(userId)
            val transactions = db.transactionDao().getByUser(userId)
            
            val displayName = user?.fullName ?: usernamePref

            // Tính số dư tổng quát
            var totalAllExp = 0.0
            var totalAllInc = 0.0
            
            // Tính báo cáo tháng này
            var monthExp = 0.0
            var monthInc = 0.0
            
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)
            
            for (t in transactions) {
                // Tính số dư (tất cả thời gian)
                if (t.type == "EXPENSE") totalAllExp += t.amount
                else totalAllInc += t.amount
                
                // Lọc cho tháng này
                calendar.timeInMillis = t.transactionDate
                if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
                    if (t.type == "EXPENSE") monthExp += t.amount
                    else monthInc += t.amount
                }
            }
            
            val balance = amount_show + (totalAllInc - totalAllExp).toLong()

            withContext(Dispatchers.Main) {
                txtWelcome.text = "Xin chào, $displayName"
                txtMoney.text = MoneyUtils.format(balance.toString(), currency)
                tvTotalExpense.text = MoneyUtils.format(monthExp.toLong().toString(), currency)
                tvTotalIncome.text = MoneyUtils.format(monthInc.toLong().toString(), currency)
                
                userPrefs.edit()
                    .putString("money", balance.toLong().toString())
                    .putString("username", displayName)
                    .apply()
            }
        }
    }
}