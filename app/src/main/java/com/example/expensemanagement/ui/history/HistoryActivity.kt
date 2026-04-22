package com.example.expensemanagement.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.main.AddExpenseActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: View
    private lateinit var tvTabExpense: TextView
    private lateinit var tvTabIncome: TextView
    private var isExpenseTab = true
    private var fullTransactionList: List<TransactionEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        tvTabExpense = findViewById(R.id.tvTabExpenseHistory)
        tvTabIncome = findViewById(R.id.tvTabIncomeHistory)
        recyclerView = findViewById(R.id.rvTransactions)
        tvEmpty = findViewById(R.id.tvEmptyData)

        tvTabExpense.setOnClickListener { switchTab(true) }
        tvTabIncome.setOnClickListener { switchTab(false) }

        setupRecyclerView()
        setupNavigation()
        setupSearch()
        
        switchTab(true) // Mặc định hiện Tiền chi
    }

    private fun switchTab(isExpense: Boolean) {
        isExpenseTab = isExpense
        if (isExpenseTab) {
            tvTabExpense.setBackgroundResource(R.drawable.bg_toggle_selected)
            tvTabExpense.setTextColor(android.graphics.Color.WHITE)
            tvTabIncome.setBackgroundResource(0)
            tvTabIncome.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
        } else {
            tvTabIncome.setBackgroundResource(R.drawable.bg_toggle_selected)
            tvTabIncome.setTextColor(android.graphics.Color.WHITE)
            tvTabExpense.setBackgroundResource(0)
            tvTabExpense.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
        }
        loadData()
    }

    private fun setupSearch() {
        val edtSearch = findViewById<EditText>(R.id.edtSearch)
        edtSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun applyFilters() {
        val query = findViewById<EditText>(R.id.edtSearch).text.toString()
        val typeFilter = if (isExpenseTab) "EXPENSE" else "INCOME"

        var filtered = fullTransactionList.filter { it.type == typeFilter }

        if (query.isNotEmpty()) {
            filtered = filtered.filter { 
                it.note?.contains(query, ignoreCase = true) == true ||
                it.amount.toString().contains(query)
            }
        }

        updateUI(filtered)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(emptyList()) { transaction ->
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("TRANSACTION_ID", transaction.id)
            startActivityForResult(intent, 100)
        }
        recyclerView.adapter = adapter
    }

    private fun loadData() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("current_user_id", -1L)

        if (userId == -1L) return

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@HistoryActivity)
            fullTransactionList = db.transactionDao().getByUser(userId)
            
            withContext(Dispatchers.Main) {
                applyFilters()
            }
        }
    }

    private fun updateUI(list: List<TransactionEntity>) {
        if (list.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.updateData(list)
        }
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.btnNavOverview)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.btnNavStatistics)?.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
            finish()
        }
        
        findViewById<View>(R.id.btnNavProfile)?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        findViewById<View>(R.id.btnNavCategories)?.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadData()
    }
}