package com.example.expensemanagement.ui.analytics

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)

        setupNavigation()
        loadDataFromDatabase()
    }

    private fun loadDataFromDatabase() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("current_user_id", -1)

        if (userId == -1L) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
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
                    updatePieChart(transactions, categoryMap)
                    updateBarChart(transactions)
                }
            }
        }
    }

    private fun updatePieChart(transactions: List<TransactionEntity>, categoryMap: Map<Long, CategoryEntity>) {
        val expenseTransactions = transactions.filter { it.type == "EXPENSE" }

        // Nhóm theo categoryId và tính tổng amount
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
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "Chi tiêu"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun updateBarChart(transactions: List<TransactionEntity>) {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)

        // Logic đơn giản: Thống kê chi tiêu theo tháng trong năm hiện tại
        val monthlyData = FloatArray(12) { 0f }

        transactions.filter { it.type == "EXPENSE" }.forEach { trans ->
            calendar.timeInMillis = trans.transactionDate
            val month = calendar.get(Calendar.MONTH)
            monthlyData[month] += trans.amount.toFloat()
        }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // Lấy 6 tháng gần nhất
        for (i in 5 downTo 0) {
            val monthIdx = (currentMonth - i + 12) % 12
            entries.add(BarEntry((5 - i).toFloat(), monthlyData[monthIdx]))
            labels.add("T${monthIdx + 1}")
        }

        val dataSet = BarDataSet(entries, "Chi tiêu hàng tháng")
        dataSet.color = Color.parseColor("#2196F3")

        val data = BarData(dataSet)
        barChart.data = data

        // Cấu hình trục X
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f

        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }

    private fun setupNavigation() {
        val btnNavOverview = findViewById<View>(R.id.btnNavOverview)
        val btnNavProfile = findViewById<View>(R.id.btnNavProfile)
        val btnNavCategories = findViewById<View>(R.id.btnNavCategories)

        btnNavOverview?.setOnClickListener {
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
        btnNavCategories?.setOnClickListener {
            startActivity(Intent(this, com.example.expensemanagement.ui.main.AddExpenseActivity::class.java))
        }
        btnNavProfile?.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })
            overridePendingTransition(0, 0)
        }
    }
}
