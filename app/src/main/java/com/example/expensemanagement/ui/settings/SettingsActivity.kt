package com.example.expensemanagement.ui.settings

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etLimit: EditText = findViewById(R.id.etSpendingLimit)
        val btnSave: Button = findViewById(R.id.btnSaveLimit)
        val btnExport: Button = findViewById(R.id.btnExportReport)

        val prefs = getSharedPreferences("ExpensePrefs", MODE_PRIVATE)
        val currentLimit = prefs.getFloat("spendingLimit", 0f)
        if (currentLimit > 0) {
            etLimit.setText(currentLimit.toString())
        }

        btnSave.setOnClickListener {
            val limitStr = etLimit.text.toString()
            if (limitStr.isNotEmpty()) {
                val limit = limitStr.toFloat()
                prefs.edit().putFloat("spendingLimit", limit).apply()
                Toast.makeText(this, "Đã lưu hạn mức $limit", Toast.LENGTH_SHORT).show()
                // Cảnh báo nếu chi vượt: Check ở màn hình Home / Transaction summary
            }
        }

        btnExport.setOnClickListener {
            // Giả lập xuất báo cáo ra file Excel / CSV (Export function)
            Toast.makeText(this, "Tính năng Xuất Báo Cáo đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }
}