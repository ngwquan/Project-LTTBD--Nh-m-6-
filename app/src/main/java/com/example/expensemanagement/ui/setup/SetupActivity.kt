package com.example.expensemanagement.ui.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.main.MainActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "")
        val spinner = findViewById<Spinner>(R.id.spinnerCurrency)
        val edtMoney = findViewById<EditText>(R.id.edtMoney)
        val btnSave = findViewById<Button>(R.id.btnSave)

        txtWelcome.text = "Xin chào, $username"

        // Tự động định dạng số tiền khi nhập
        edtMoney.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    edtMoney.removeTextChangedListener(this)

                    // Loại bỏ tất cả các dấu chấm trước khi xử lý
                    val cleanString = s.toString().replace(".", "")

                    if (cleanString.isNotEmpty()) {
                        try {
                            val parsed = cleanString.toDouble()
                            // Định dạng số với dấu chấm phân cách hàng nghìn
                            val formatter = NumberFormat.getInstance(Locale("vi", "VN")) as DecimalFormat
                            formatter.applyPattern("#,###")
                            val formatted = formatter.format(parsed).replace(",", ".")

                            current = formatted
                            edtMoney.setText(formatted)
                            edtMoney.setSelection(formatted.length)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        current = ""
                    }

                    edtMoney.addTextChangedListener(this)
                }
            }
        })

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

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("MONEY", currency)
            intent.putExtra("CURRENCY", currency)
            startActivity(intent)

            finish()
        }
    }
}