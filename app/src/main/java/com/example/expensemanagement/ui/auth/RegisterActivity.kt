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
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
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
            val username = edtUsername.text.toString().trim()

            // 1. Kiểm tra đầu vào

            // Validate họ và tên
            if (name.isEmpty()) {
                edtName.error = "Vui lòng nhập họ tên"
                return@setOnClickListener
            }
            if (!name[0].isLetter()) {
                edtName.error = "Ký tự đầu phải là chữ cái!"
                return@setOnClickListener
            }
            if (name.any { it.isDigit() }) {
                edtName.error = "Tên không được chứa số!"
                return@setOnClickListener
            }

            // Validate username
            val usernameRegex = Regex("^[a-zA-Z0-9_]{4,20}$")

            if (username.isEmpty()) {
                edtUsername.error = "Vui lòng nhập tên đăng nhập"
                return@setOnClickListener
            }
            if (!usernameRegex.matches(username)) {
                edtUsername.error = "Username 4-20 ký tự, chỉ gồm chữ, số, _"
                return@setOnClickListener
            }
            if (username.contains(" ")) {
                edtUsername.error = "Username không được chứa khoảng trắng"
                return@setOnClickListener
            }

            // Validate email
            if (email.isEmpty()) {
                edtEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.error = "Email không hợp lệ"
                return@setOnClickListener
            }

            // Validate mật khẩu
            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

            if (password.isEmpty()) {
                edtPassword.error = "Vui lòng nhập mật khẩu"
                return@setOnClickListener
            }
            if (!passwordRegex.matches(password)) {
                edtPassword.error = "Mật khẩu ≥8 ký tự, có chữ hoa, thường và số"
                return@setOnClickListener
            }

            // Xác nhận lại mật khẩu
            if (password != confirm) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fun hashPassword(password: String): String {
                val bytes = password.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                return digest.joinToString("") { "%02x".format(it) }
            }
            // 2. Thực hiện đăng ký
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Kiểm tra email tồn tại
                    val existingUser = userDao.getUserByEmail(email)

                    if (existingUser != null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Email này đã được đăng ký!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Tạo và lưu user mới
                        val hashedPassword = hashPassword(password)
                        val newUser = UserEntity(
                            fullName = name,
                            username = username,
                            email = email,
                            passwordHash = hashedPassword )
                        userDao.insert(newUser)

                        // Lưu vào SharedPreferences để đồng bộ với logic LoginActivity cũ (nếu cần)
                        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("email", email)
                            putString("password", hashedPassword)
                            putString("username", username)
                            apply()
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Đăng ký thành công!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Lỗi: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
