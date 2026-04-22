package com.example.expensemanagement.ui.main

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R

class AddExpenseActivity : AppCompatActivity() {

    private var currentAmount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val tvAmount = findViewById<TextView>(R.id.tvAmountDisplay)
        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        val btnSave = findViewById<Button>(R.id.btnSaveExpense)

        // Spinner loại giao dịch
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_type,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter


        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                val number = (it as Button).text.toString()
                currentAmount += number
                tvAmount.text = currentAmount
            }
        }

        // Clear
        findViewById<Button>(R.id.btnC).setOnClickListener {
            currentAmount = ""
            tvAmount.text = "0"
        }

        btnSave.setOnClickListener {

            if (currentAmount.isEmpty()) {
                Toast.makeText(this, "Nhập số tiền", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = currentAmount.toLong()
            val type = spinnerType.selectedItem.toString()

            val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = globalPref.getLong("current_user_id", -1)
            val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)

            val currentMoney = userPrefs.getString("money", "0")?.toLongOrNull() ?: 0

            val newMoney = if (type == "Chi tiêu") {
                currentMoney - amount
            } else {
                currentMoney + amount
            }

            userPrefs.edit()
                .putString("money", newMoney.toString())
                .apply()

            val oldHistory = userPrefs.getString("history", "") ?: ""
            val newRecord = "$type - $amount\n"

            userPrefs.edit()
                .putString("history", oldHistory + newRecord)
                .apply()

            Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}