package com.example.expensemanagement.ui.main

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.databinding.ActivityAddExpenseBinding
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root as View)

        binding.tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                binding.tvDate.text = "$day/${month + 1}/$year"
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnSaveExpense.setOnClickListener {
            val amount = binding.edtAmount.text.toString()
            if (amount.isNotEmpty()) {
                // Logic lưu chi tiêu
                finish()
            }
        }
    }
}