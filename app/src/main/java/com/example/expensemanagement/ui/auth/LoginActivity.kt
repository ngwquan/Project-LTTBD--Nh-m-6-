package com.example.expensemanagement.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.HomeActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lấy thông tin đã đăng ký
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val savedEmail = sharedPref.getString("email", null)
            val savedPassword = sharedPref.getString("password", null)

            // Tài khoản để vào thẳng mà không cần đăng ký
            val isDefaultAdmin = (email == "admin" && password == "123456")
            val isRegisteredUser = (email == savedEmail && password == savedPassword)

            if (isDefaultAdmin || isRegisteredUser) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}