package com.example.expensemanagement.ui.auth

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtConfirm = findViewById<EditText>(R.id.edtConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirm = edtConfirm.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lưu thông tin đăng ký vào localStorage
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("email", email)
                putString("password", password)
                apply()
            }

            // thông báo đng ký thành công
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()

            finish()
        }
    }
}