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
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)
        val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        
        val username = userPrefs.getString("username", "User")
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

                    val cleanString = s.toString().replace(".", "")

                    if (cleanString.isNotEmpty()) {
                        try {
                            val parsed = cleanString.toDouble()
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

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection (0)

        btnSave.setOnClickListener {
            val moneyFormatted = edtMoney.text.toString()
            if (moneyFormatted.isEmpty()) {
                edtMoney.error = "Vui lòng nhập số tiền"
                return@setOnClickListener
            }

            val rawMoney = moneyFormatted.replace(".", "")
            val currency = spinner.selectedItem.toString()

            // Lưu dữ liệu vào SharedPreferences của User hiện tại
            userPrefs.edit()
                .putString("money", rawMoney)
                .putString("currency", currency)
                .putBoolean("isSetupDone", true) // Đánh dấu đã setup xong
                .commit() // Dùng commit để ghi ngay lập tức

            Toast.makeText(this, "Thiết lập thành công!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}