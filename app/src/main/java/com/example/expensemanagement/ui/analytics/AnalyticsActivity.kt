package com.example.expensemanagement.ui.analytics

import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var rvCategoryDetails: RecyclerView
    private lateinit var categoryAdapter: CategoryAnalyticsAdapter
    private lateinit var tvProfitLossStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)
        rvCategoryDetails = findViewById(R.id.rvCategoryDetails)
        tvProfitLossStatus = findViewById(R.id.tvProfitLossStatus)

        setupRecyclerView()
        setupNavigation()
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAnalyticsAdapter(emptyList())
        rvCategoryDetails.layoutManager = LinearLayoutManager(this)
        rvCategoryDetails.adapter = categoryAdapter
    }

    override fun onResume() {
        super.onResume()
        loadDataFromDatabase()
    }

    private fun loadDataFromDatabase() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("current_user_id", -1)

        if (userId == -1L) {
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AnalyticsActivity)
            val transactions = db.transactionDao().getByUser(userId)
            val categories = db.categoryDao().getByUser(userId)
            val categoryMap = categories.associateBy { it.id }

            withContext(Dispatchers.Main) {
                if (transactions.isEmpty()) {
                    findViewById<View>(R.id.analyticsContent).visibility = View.GONE
                    findViewById<View>(R.id.llEmptyState).visibility = View.VISIBLE
                } else {
                    findViewById<View>(R.id.analyticsContent).visibility = View.VISIBLE
                    findViewById<View>(R.id.llEmptyState).visibility = View.GONE
                    updateProfitLossStatus(transactions)
                    updatePieChart(transactions, categoryMap)
                    updateBarChart(transactions)
                    updateCategoryList(transactions, categoryMap)
                }
            }
        }
    }

    private fun updateProfitLossStatus(transactions: List<TransactionEntity>) {
        var totalIncome = 0.0
        var totalExpense = 0.0

        for (t in transactions) {
            when (t.type) {
                "INCOME" -> totalIncome += t.amount
                "EXPENSE" -> totalExpense += t.amount
            }
        }

        val balance = totalIncome - totalExpense
        if (balance > 0) {
            tvProfitLossStatus.text = "Lãi: \$${String.format(Locale.US, "%.2f", balance)}"
            tvProfitLossStatus.setTextColor(Color.parseColor("#4CAF50")) // Xanh lá cây
        } else if (balance < 0) {
            tvProfitLossStatus.text = "Lỗ: \$${String.format(Locale.US, "%.2f", Math.abs(balance))}"
            tvProfitLossStatus.setTextColor(Color.parseColor("#F44336")) // Đỏ
        } else {
            tvProfitLossStatus.text = "Cân bằng: \$0.00"
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

    private fun setupNavigation() {
        findViewById<View>(R.id.btnNavOverview)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.btnNavHistory)?.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
        findViewById<View>(R.id.btnNavCategories)?.setOnClickListener {
            startActivity(Intent(this, com.example.expensemanagement.ui.main.AddExpenseActivity::class.java))
        }
        findViewById<View>(R.id.btnNavStatistics)?.setOnClickListener {
            // Đã ở trang thống kê, có thể scroll lên đầu
        }
        findViewById<View>(R.id.btnNavProfile)?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
    }
}
