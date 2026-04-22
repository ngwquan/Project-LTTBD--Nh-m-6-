package com.example.expensemanagement.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.setup.SetupActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)
        val imgToggle = findViewById<ImageView>(R.id.imgToggle)
        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        // xử lý hiện/ẩn mật khẩu
        imgToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imgToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imgToggle.setImageResource(android.R.drawable.ic_menu_view)
            }
            edtPassword.setSelection(edtPassword.text.length)
        }

        // xử lý sự kiện Đăng nhập
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sử dụng Coroutine để kiểm tra Database
            lifecycleScope.launch(Dispatchers.IO) {
                // Kiểm tra trong Database
                val user = userDao.getUserByEmail(email)

                withContext(Dispatchers.Main) {
                    if (user != null && user.passwordHash == password) {
                        proceedToSetup(user.id)
                    } else {
                        Toast.makeText(this@LoginActivity, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // chuyển sang màn hình Đăng ký
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun proceedToSetup(userId: Long) {

        val sharedPref = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)

        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        globalPref.edit()
            .putLong("current_user_id", userId)
            .apply()

        val isSetupDone = sharedPref.getBoolean("isSetupDone", false)

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

        if (isSetupDone) {
            startActivity(Intent(this, com.example.expensemanagement.ui.main.MainActivity::class.java))
        } else {
            startActivity(Intent(this, SetupActivity::class.java))
        }

        finish()
    }
}