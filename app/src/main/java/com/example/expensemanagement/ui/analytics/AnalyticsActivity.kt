package com.example.expensemanagement.ui.analytics

import android.content.Intent
import android.widget.LinearLayout
import android.widget.Toast
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.history.HistoryActivity
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.ui.profile.ProfileActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class AnalyticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        val pieChart: PieChart = findViewById(R.id.pieChart)
        val barChart: BarChart = findViewById(R.id.barChart)

        setupPieChart(pieChart)
        setupBarChart(barChart)

        // Bottom Navigation Click Listeners
        val btnNavExpense = findViewById<LinearLayout>(R.id.btnNavExpense)
        val btnNavStatistics = findViewById<LinearLayout>(R.id.btnNavStatistics)
        val btnNavProfile = findViewById<LinearLayout>(R.id.btnNavProfile)
        val fabAdd = findViewById<android.view.View>(R.id.fab_add)

        // --- Nút CHI TIÊU ---
        btnNavExpense?.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // --- Nút THỐNG KÊ ---
        btnNavStatistics?.setOnClickListener {
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }

        // --- Nút PROFILE ---
        btnNavProfile?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // --- Nút FAB (Mở trang thêm chi tiêu) ---
        fabAdd?.setOnClickListener {
            val intent = Intent(this, com.example.expensemanagement.ui.main.AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupPieChart(pieChart: PieChart) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "Ăn uống"))
        entries.add(PieEntry(30f, "Giải trí"))
        entries.add(PieEntry(20f, "Đi lại"))
        entries.add(PieEntry(10f, "Khác"))

        val dataSet = PieDataSet(entries, "Danh mục chi tiêu")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.invalidate() 
    }

    private fun setupBarChart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 1500f)) 
        entries.add(BarEntry(2f, 2000f))
        entries.add(BarEntry(3f, 1800f))
        entries.add(BarEntry(4f, 2200f))

        val dataSet = BarDataSet(entries, "Chi tiêu hàng tháng")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = BarData(dataSet)
        barChart.data = data
        barChart.description.isEnabled = false
        barChart.invalidate()
    }
}