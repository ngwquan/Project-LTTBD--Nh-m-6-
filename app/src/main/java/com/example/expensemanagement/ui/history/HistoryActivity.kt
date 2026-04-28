package com.example.expensemanagement.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.model.TransactionWithCategory
import com.example.expensemanagement.ui.main.AddExpenseActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.ui.analytics.AnalyticsActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var tvTabExpense: TextView
    private lateinit var tvTabIncome: TextView
    private lateinit var edtSearch: EditText
    private lateinit var tvEmptyData: TextView

    private var isExpenseMode = true
    private var fullList = listOf<TransactionWithCategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // 1. Ánh xạ View
        initViews()

        // 2. Thiết lập RecyclerView
        setupRecyclerView()

        // 3. Sự kiện chuyển Tab Tiền chi / Tiền thu
        tvTabExpense.setOnClickListener { switchTab(true) }
        tvTabIncome.setOnClickListener { switchTab(false) }

        // 4. Sự kiện tìm kiếm động
        setupSearch()

        // 5. Lắng nghe dữ liệu từ Database (Sử dụng Flow để cập nhật Real-time)
        observeTransactions()

        // 6. Xử lý điều hướng Bottom Navigation ĐỘNG
        setupBottomNavigation()
    }

    private fun initViews() {
        rvTransactions = findViewById(R.id.rvTransactions)
        tvTabExpense = findViewById(R.id.tvTabExpenseHistory)
        tvTabIncome = findViewById(R.id.tvTabIncomeHistory)
        edtSearch = findViewById(R.id.edtSearch)
        tvEmptyData = findViewById(R.id.tvEmptyData)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) {
            item -> val intent = Intent(this, TransactionDetailActivity::class.java)
            intent.putExtra("transactionId", item.id)

            intent.putExtra("category", item.categoryName)
            intent.putExtra("amount", item.amount.toLong())
            intent.putExtra("date", item.date)
            intent.putExtra("note", item.note)
            intent.putExtra("type", item.type)
            startActivity(intent)
        }
        rvTransactions.layoutManager = LinearLayoutManager(this)
        rvTransactions.adapter = transactionAdapter
    }

    private fun setupBottomNavigation() {
        // Nút Tổng quan
        findViewById<LinearLayout>(R.id.btnNavOverview).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Nút Thêm mới (Màn hình thêm chi tiêu chúng ta vừa làm)
        findViewById<LinearLayout>(R.id.btnNavCategories).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // Nút Thống kê
        findViewById<LinearLayout>(R.id.btnNavStatistics).setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
        }

        // Nút Profile
        findViewById<LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Nút Lịch sử (Chính là trang này, có thể không cần code hoặc chỉ scroll lên đầu)
        findViewById<LinearLayout>(R.id.btnNavHistory).setOnClickListener {
            rvTransactions.smoothScrollToPosition(0)
        }
    }

    private fun switchTab(isExpense: Boolean) {
        isExpenseMode = isExpense
        if (isExpense) {
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
        filterAndDisplay(edtSearch.text.toString())
    }

    private fun observeTransactions() {
        val userId = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getLong("current_user_id", -1)
        if (userId == -1L) return

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@HistoryActivity)
            db.transactionDao().getAllTransactionsWithCategory(userId).collectLatest { list ->
                fullList = list
                withContext(Dispatchers.Main) {
                    filterAndDisplay(edtSearch.text.toString())
                }
            }
        }
    }

    private fun filterAndDisplay(query: String) {
        val type = if (isExpenseMode) "EXPENSE" else "INCOME"
        val filteredList = fullList.filter {
            it.type == type && (it.categoryName.contains(query, true) || it.note.contains(query, true))
        }
        transactionAdapter.updateData(filteredList)
        tvEmptyData.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setupSearch() {
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndDisplay(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}