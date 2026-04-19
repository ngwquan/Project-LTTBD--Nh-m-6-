package com.example.expensemanagement.ui.auth

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtConfirm = findViewById<EditText>(R.id.edtConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        txtLogin.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val confirm = edtConfirm.text.toString().trim()

            // 1. Kiểm tra đầu vào
            if (email.isEmpty()) {
                edtEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập họ tên"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                edtPassword.error = "Vui lòng nhập mật khẩu"
                return@setOnClickListener
            }
            if (password != confirm) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Thực hiện đăng ký
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Kiểm tra email tồn tại
                    val existingUser = userDao.getUserByEmail(email)
                    
                    if (existingUser != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Email này đã được đăng ký!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Tạo và lưu user mới
                        val newUser = UserEntity(
                            fullName = name,
                            email = email,
                            passwordHash = password // Lưu trực tiếp theo yêu cầu project hiện tại
                        )
                        userDao.insert(newUser)

                        // Lưu vào SharedPreferences để đồng bộ với logic LoginActivity cũ (nếu cần)
                        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("email", email)
                            putString("password", password)
                            apply()
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
