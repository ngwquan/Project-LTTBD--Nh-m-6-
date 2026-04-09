package com.example.expensemanagement.ui.amount

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R

class AmountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amount)

        val edtMoney = findViewById<EditText>(R.id.edtMoney)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val money = edtMoney.text.toString()

            if (money.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Số tiền: $money", Toast.LENGTH_SHORT).show()
            }
        }
    }
}