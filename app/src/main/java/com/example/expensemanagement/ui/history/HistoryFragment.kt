package com.example.expensemanagement.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.model.TransactionWithCategory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var tvTabExpense: TextView
    private lateinit var tvTabIncome: TextView
    private lateinit var edtSearch: EditText
    private lateinit var tvEmptyData: TextView

    private var isExpenseMode = true
    private var fullList = listOf<TransactionWithCategory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Ánh xạ View
        initViews(view)

        // 2. Thiết lập RecyclerView
        setupRecyclerView()

        // 3. Sự kiện chuyển Tab Tiền chi / Tiền thu
        tvTabExpense.setOnClickListener { switchTab(true) }
        tvTabIncome.setOnClickListener { switchTab(false) }

        // 4. Sự kiện tìm kiếm động
        setupSearch()

        // 5. Lắng nghe dữ liệu từ Database (Sử dụng Flow để cập nhật Real-time)
        observeTransactions()
    }

    private fun initViews(view: View) {
        rvTransactions = view.findViewById(R.id.rvTransactions)
        tvTabExpense = view.findViewById(R.id.tvTabExpenseHistory)
        tvTabIncome = view.findViewById(R.id.tvTabIncomeHistory)
        edtSearch = view.findViewById(R.id.edtSearch)
        tvEmptyData = view.findViewById(R.id.tvEmptyData)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) {
            item -> val intent = Intent(requireContext(), TransactionDetailActivity::class.java)
            intent.putExtra("transactionId", item.id)

            intent.putExtra("category", item.categoryName)
            intent.putExtra("amount", item.amount.toLong())
            intent.putExtra("date", item.date)
            intent.putExtra("note", item.note)
            intent.putExtra("type", item.type)

            startActivity(intent)
        }
        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        rvTransactions.adapter = transactionAdapter
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
        val context = requireContext()

        val userId = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getLong("current_user_id", -1)
        if (userId == -1L) return

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
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