package com.example.expensemanagement.ui.auth

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import androidx.lifecycle.lifecycleScope
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

            if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sử dụng Coroutine để thao tác với Database
            lifecycleScope.launch(Dispatchers.IO) {
                // 1. Kiểm tra email đã tồn tại chưa
                val existingUser = userDao.getUserByEmail(email)

                withContext(Dispatchers.Main) {
                    if (existingUser != null) {
                        Toast.makeText(this@RegisterActivity, "Email này đã được đăng ký!", Toast.LENGTH_SHORT).show()
                    } else {
                        // 2. Nếu chưa tồn tại, tiến hành lưu vào Database
                        saveUserToDatabase(email, password, name)
                    }
                }
            }
        }
    }
    private fun saveUserToDatabase(email: String, password: String, name:String) {
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val newUser = UserEntity(
                email = email,
                passwordHash = password,
                fullName = name
            )
            userDao.insert(newUser)

            withContext(Dispatchers.Main) {
                // Đồng thời vẫn lưu vào SharedPreferences để giữ logic cũ nếu cần (tùy chọn)
                val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("fullName", name)
                    putString("email", email)
                    putString("password", password)
                    apply()
                }

                Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}