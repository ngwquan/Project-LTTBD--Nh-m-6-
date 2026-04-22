package com.example.expensemanagement.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
    private var fullTransactionList: List<TransactionEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setupRecyclerView()
        setupNavigation()
        setupSearch()
        loadData()
    }

    private fun setupSearch() {
        val edtSearch = findViewById<android.widget.EditText>(R.id.edtSearch)
        edtSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        val chipGroup = findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupFilter)
        chipGroup.setOnCheckedChangeListener { _, _ ->
            applyFilters()
        }
    }

    private fun applyFilters() {
        val query = findViewById<android.widget.EditText>(R.id.edtSearch).text.toString()
        val checkedChipId = findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupFilter).checkedChipId

        var filtered = fullTransactionList

        // Lọc theo text
        if (query.isNotEmpty()) {
            filtered = filtered.filter { 
                it.note?.contains(query, ignoreCase = true) == true ||
                it.amount.toString().contains(query)
            }
        }

        // Lọc theo loại (Chip)
        filtered = when (checkedChipId) {
            R.id.chipExpense -> filtered.filter { it.type == "EXPENSE" }
            R.id.chipIncome -> filtered.filter { it.type == "INCOME" }
            else -> filtered
        }

        updateUI(filtered)
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvTransactions)
        tvEmpty = findViewById(R.id.tvEmptyData)
        
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
                updateUI(fullTransactionList)
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
        val btnNavOverview = findViewById<android.view.View>(R.id.btnNavOverview)
        val btnNavExpense = findViewById<android.view.View>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<android.view.View>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<android.view.View>(R.id.btnNavProfile)
        val fabAdd = findViewById<android.view.View>(R.id.fab_add)

        btnNavOverview?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
        
        btnNavStatistics?.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
        
        btnNavProfile?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }

        fabAdd?.setOnClickListener {
            startActivityForResult(Intent(this, AddExpenseActivity::class.java), 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            loadData()
        }
    }
}