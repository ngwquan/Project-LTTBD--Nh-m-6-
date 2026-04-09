package com.example.expensemanagement.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.amount.AmountActivity
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val spinner = findViewById<Spinner>(R.id.spinnerCurrency)

        val currencyList = arrayOf(
            "VND - Việt Nam Đồng",
            "USD - Đô la Mỹ",
            "EUR - Euro",
            "JPY - Yên Nhật",
            "CNY - Nhân dân tệ",
            "AUD - Đô la Úc",
            "CAD - Đô la Canada",
            "CHF - Frank THuỵ Sĩ",
            "KRW - Won Hàn Quốc"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            currencyList
        )

        spinner.adapter = adapter

        val btn = findViewById<Button>(R.id.btnContinue)

        btn.setOnClickListener {
            val selected = spinner.selectedItem.toString()

            Toast.makeText(this, "$selected", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AmountActivity::class.java)
            startActivity(intent)
        }
    }
}