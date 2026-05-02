package com.example.expensemanagement.ui.analytics

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.utils.MoneyUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import java.util.*

class AnalyticsFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var rvCategoryDetails: RecyclerView
    private lateinit var categoryAdapter: CategoryAnalyticsAdapter
    private lateinit var tvProfitLossStatus: TextView
    private lateinit var analyticsContent: View
    private lateinit var emptyState: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_analytics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
    }

    private fun initViews(view: View) {
        pieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChart)
        rvCategoryDetails = view.findViewById(R.id.rvCategoryDetails)
        tvProfitLossStatus = view.findViewById(R.id.tvProfitLossStatus)

        analyticsContent = view.findViewById(R.id.analyticsContent)
        emptyState = view.findViewById(R.id.llEmptyState)
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDatabase()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAnalyticsAdapter(emptyList())
        rvCategoryDetails.layoutManager = LinearLayoutManager(requireContext())
        rvCategoryDetails.adapter = categoryAdapter
    }


    private fun loadDataFromDatabase() {
        val context = requireContext()

        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("current_user_id", -1)

        if (userId == -1L) {
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            val transactions = db.transactionDao().getByUser(userId)
            val categories = db.categoryDao().getByUser(userId)
            val categoryMap = categories.associateBy { it.id }

            withContext(Dispatchers.Main) {
                updateUI(transactions, categoryMap)
            }
        }
    }

    private fun updateUI(
        transactions: List<TransactionEntity>,
        categoryMap: Map<Long, CategoryEntity>
    ) {
        if(transactions.isEmpty()) {
            analyticsContent.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
            return
        }
        analyticsContent.visibility = View.VISIBLE
        emptyState.visibility = View.GONE

        updateProfitLossStatus(transactions)
        updatePieChart(transactions, categoryMap)
        updateBarChart(transactions)
        updateCategoryList(transactions, categoryMap)
        Log.d("UI_CHECK", "updateUI called with size = ${transactions.size}")
    }

    private fun updateProfitLossStatus(transactions: List<TransactionEntity>) {
        var totalIncome = 0.0
        var totalExpense = 0.0

        val context = requireContext()
        val globalPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)

        val userPrefs = context.getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val currency = userPrefs.getString("currency", "₫") ?: "₫"

        for (t in transactions) {
            when (t.type.trim().uppercase())  {
                "INCOME" -> totalIncome += t.amount
                "EXPENSE" -> totalExpense += t.amount
            }
        }

        val balance = (totalIncome - totalExpense).toLong()
        Log.d("Transaction", "Income: $totalIncome")
        Log.d("Transaction", "Expense: $totalExpense")
        Log.d("Transaction", "Balance: $balance")
        Log.d("Transaction", "Size: ${transactions.size}")
        if (balance > 0) {
            tvProfitLossStatus.text = "Lãi: ${MoneyUtils.format(balance.toString(), currency)}"
            tvProfitLossStatus.setTextColor(Color.parseColor("#4CAF50")) // Xanh lá cây
        } else if (balance < 0) {
            tvProfitLossStatus.text = "Lỗ: ${MoneyUtils.format(Math.abs(balance).toString(), currency)}"
            tvProfitLossStatus.setTextColor(Color.parseColor("#F44336")) // Đỏ
        } else {
            tvProfitLossStatus.text = "Cân bằng: 0"
            tvProfitLossStatus.setTextColor(Color.parseColor("#757575")) // Xám
        }
    }

    private fun updateCategoryList(transactions: List<TransactionEntity>, categoryMap: Map<Long, CategoryEntity>) {
        // Nhóm và tính tổng theo từng danh mục
        val totalsByCategory = transactions.groupBy { it.categoryId }
            .mapValues { entry -> 
                entry.value.sumOf { if (it.type == "EXPENSE") -it.amount else it.amount }
            }

        val summaryList = totalsByCategory.map { (catId, total) ->
            val category = categoryMap[catId]
            val name = category?.name ?: "Khác"
            val color = getCategoryColor(name)
            CategorySummary(name, total, color, 0f)
        }.sortedBy { it.amount } // Hiển thị chi tiêu (âm) nhiều nhất hoặc thu nhập ít nhất

        categoryAdapter.updateData(summaryList)
    }

    private fun updatePieChart(transactions: List<TransactionEntity>, categoryMap: Map<Long, CategoryEntity>) {
        val expenseTransactions = transactions.filter { it.type == "EXPENSE" }

        val totalsByCategory = expenseTransactions.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = ArrayList<PieEntry>()
        totalsByCategory.forEach { (catId, total) ->
            val categoryName = categoryMap[catId]?.name ?: "Khác"
            entries.add(PieEntry(total.toFloat(), categoryName))
        }

        if (entries.isEmpty()) {
            pieChart.clear()
            pieChart.setNoDataText("Chưa có dữ liệu chi tiêu")
            pieChart.invalidate()
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "Cơ cấu chi tiêu"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun updateBarChart(transactions: List<TransactionEntity>) {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)

        val monthlyExpense = FloatArray(12) { 0f }
        val monthlyIncome = FloatArray(12) { 0f }

        transactions.forEach { trans ->
            calendar.timeInMillis = trans.transactionDate
            val month = calendar.get(Calendar.MONTH)
            if (trans.type == "EXPENSE") {
                monthlyExpense[month] += trans.amount.toFloat()
            } else {
                monthlyIncome[month] += trans.amount.toFloat()
            }
        }

        val expEntries = ArrayList<BarEntry>()
        val incEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        for (i in 5 downTo 0) {
            val monthIdx = (currentMonth - i + 12) % 12
            expEntries.add(BarEntry((5 - i).toFloat(), monthlyExpense[monthIdx]))
            incEntries.add(BarEntry((5 - i).toFloat(), monthlyIncome[monthIdx]))
            labels.add("T${monthIdx + 1}")
        }

        val expDataSet = BarDataSet(expEntries, "Chi tiêu")
        expDataSet.color = Color.parseColor("#F44336")

        val incDataSet = BarDataSet(incEntries, "Thu nhập")
        incDataSet.color = Color.parseColor("#4CAF50")

        val data = BarData(incDataSet, expDataSet)
        data.barWidth = 0.35f
        barChart.data = data
        barChart.groupBars(-0.5f, 0.3f, 0.02f) 

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.axisMinimum = -0.5f
        xAxis.axisMaximum = 5.5f
        xAxis.setCenterAxisLabels(true)

        barChart.description.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun getCategoryColor(name: String): String {
        return when (name) {
            "Ăn uống" -> "#FFA500"
            "Đi lại" -> "#A52A2A"
            "Mua sắm" -> "#0000FF"
            "Y tế" -> "#00FF7F"
            "Giáo dục" -> "#FF4500"
            "Tiền điện" -> "#00BFFF"
            "Mỹ phẩm" -> "#FF69B4"
            "Lương" -> "#4CAF50"
            "Số dư đầu" -> "#4DB6E2"
            "Chi tiêu khác" -> "#9E9E9E"
            "Thu nhập khác" -> "#8BC34A"
            else -> "#757575"
        }
    }
}
