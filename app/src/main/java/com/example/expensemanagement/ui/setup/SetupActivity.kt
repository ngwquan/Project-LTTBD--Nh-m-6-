package com.example.expensemanagement.ui.setup

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.setup.SetupActivity
import android.widget.EditText

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val spinner = findViewById<Spinner>(R.id.spinnerCurrency)
        val edtMoney = findViewById<EditText>(R.id.edtMoney)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // spinner chọn đơn vị tiền
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection (0)

        // button bắt đầu sử dụng
        btnSave.setOnClickListener {

            val money = edtMoney.text.toString()

            if (money.isEmpty()) {
                edtMoney.error = "Vui lòng nhập số tiền"
                return@setOnClickListener
            }

            val currency = spinner.selectedItem.toString()

            Toast.makeText(this, "Đã lưu: $money - $currency", Toast.LENGTH_SHORT).show()
        }
    }
}