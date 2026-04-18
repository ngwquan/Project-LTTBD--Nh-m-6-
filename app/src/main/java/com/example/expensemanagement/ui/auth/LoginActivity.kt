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

//            // lấy dữ liệu đã đăng ký từ SharedPreferences
//            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//            val savedEmail = sharedPref.getString("email", null)
//            val savedPassword = sharedPref.getString("password", null)
//
//            // kiểm tra tài khoản (Admin mặc định hoặc tài khoản đã đăng ký)
//            val isDefaultAdmin = (email == "admin" && password == "admin")
//            val isRegisteredUser = (email == savedEmail && password == savedPassword)
//
//            if (isDefaultAdmin || isRegisteredUser) {
//                // Lưu trạng thái đã đăng nhập (nếu cần dùng cho MainActivity check)
//                if (isDefaultAdmin && savedEmail == null) {
//                    with(sharedPref.edit()) {
//                        putString("email", email)
//                        apply()
//                    }
//                }
//
//                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this, SetupActivity::class.java))
//                finish()
//            } else {
//                Toast.makeText(this, "Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show()
//            }
            // Sử dụng Coroutine để kiểm tra Database
            lifecycleScope.launch(Dispatchers.IO) {
                // 1. Kiểm tra tài khoản admin mặc định
                if (email == "admin" && password == "admin") {
                    withContext(Dispatchers.Main) {
                        proceedToSetup(-1) // ID -1 đại diện cho Admin
                    }
                    return@launch
                }

                // 2. Kiểm tra trong Database
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
        // Lưu userId vào SharedPreferences để các màn hình sau (Setup, Main) biết ai đang dùng
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("current_user_id", userId)
            apply()
        }

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SetupActivity::class.java)
        startActivity(intent)
        finish()
    }
}