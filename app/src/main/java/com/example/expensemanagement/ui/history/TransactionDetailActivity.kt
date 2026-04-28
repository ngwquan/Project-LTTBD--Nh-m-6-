package com.example.expensemanagement.ui.history

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlertDialog
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        val tvTransactionId = findViewById<TextView>(R.id.tvTransactionId)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvAmount = findViewById<TextView>(R.id.tvAmount)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvNote = findViewById<TextView>(R.id.tvNote)

        val btnDelete = findViewById<FloatingActionButton>(R.id.fabDelete)
        val transactionId = intent.getLongExtra("transactionId", -1L)


        // Nhận dữ liệu từ Intent
        val category = intent.getStringExtra("category") ?: ""
        val amount = intent.getLongExtra("amount", 0L)
        val date = intent.getLongExtra("date", 0L)
        val note = intent.getStringExtra("note") ?: ""
        val type = intent.getStringExtra("type") ?: "EXPENSE"

        // Set dữ liệu
        tvTransactionId.text = transactionId.toString()
        tvCategory.text = category
        tvNote.text = if (note.isEmpty()) "Không có" else note

        // Format ngày
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvDate.text = sdf.format(Date(date))

        // Format tiền + màu
        val formattedAmount = "%,d đ".format(amount)

        if (type == "EXPENSE") {
            tvAmount.text = "- $formattedAmount"
            tvAmount.setTextColor(Color.RED)
        } else {
            tvAmount.text = "+ $formattedAmount"
            tvAmount.setTextColor(Color.parseColor("#4CAF50"))
        }

        btnDelete.setOnClickListener {
            if (transactionId == -1L) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn xóa giao dịch này?")
                .setPositiveButton("Xóa") { _, _ ->
                    deleteTransaction(transactionId)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }
    private fun deleteTransaction(id: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@TransactionDetailActivity)

            db.transactionDao().deleteById(id)

            runOnUiThread {
                finish() // quay lại History
            }
        }
    }
}