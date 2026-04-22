package com.example.expensemanagement.ui.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.main.MainActivity
import com.example.expensemanagement.utils.MoneyUtils

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val spinner = findViewById<Spinner>(R.id.spinnerCurrency)
        val edtMoney = findViewById<EditText>(R.id.edtMoney)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)

        val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val username = userPrefs.getString("username", "User")

        txtWelcome.text = getString(R.string.welcome_user, username)

        // Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnSave.setOnClickListener {

            val money = edtMoney.text.toString()

            if (money.isEmpty()) {
                edtMoney.error = "Vui lòng nhập số tiền"
                return@setOnClickListener
            }

            val currency = spinner.selectedItem.toString()

            userPrefs.edit()
                .putString("money", money)
                .putString("currency", currency)
                .putBoolean("isSetupDone", true)
                .apply()

            val formattedMoney = MoneyUtils.format(money, currency)

            Toast.makeText(this, "Đã lưu: $formattedMoney", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}