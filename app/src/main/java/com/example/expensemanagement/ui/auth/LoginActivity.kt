package com.example.expensemanagement.ui.auth

import com.example.expensemanagement.data.local.dao.UserDao
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.setup.SetupActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.data.local.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod


class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = globalPref.getBoolean("is_logged_in", false)
        val userId = globalPref.getLong("current_user_id", -1)

        if (isLoggedIn && userId != -1L) {
            proceedToSetup(userId)
            return
        }
        val edtEmailorUsername = findViewById<EditText>(R.id.edtEmailorUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)
        val imgToggle = findViewById<ImageView>(R.id.imgToggle)
        val db = AppDatabase.getDatabase(this)
        val rememberCheckBox = findViewById<CheckBox>(R.id.rememberLogin)
        val txtForget = findViewById<TextView>(R.id.forgetPassword)

        // xử lý hiện/ẩn mật khẩu
        imgToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imgToggle.setImageResource(R.drawable.invisible_ic_eye)
            } else {
                edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                imgToggle.setImageResource(R.drawable.visible_ic_eye)
            }
            edtPassword.setSelection(edtPassword.text.length)
        }


        // Xử lý sự kiện quên mật khẩu
        txtForget.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
        // xử lý sự kiện Đăng nhập
        btnLogin.setOnClickListener {
            val EmailOrUsername = edtEmailorUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (EmailOrUsername.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sử dụng Coroutine để kiểm tra Database
            lifecycleScope.launch(Dispatchers.IO) {
                userDao = db.userDao()
                // Kiểm tra trong Database
                val user = userDao.getUserByEmailOrUsername(EmailOrUsername)
                val hashedInput = hashPassword(password)

                withContext(Dispatchers.Main) {
                    if (user != null && user.passwordHash == hashedInput) {
                        // Lưu thông tin username trước khi chuyển màn hình
                        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        globalPref.edit().apply {
                            putLong("current_user_id", user.id)
                            putBoolean("is_logged_in", rememberCheckBox.isChecked)
                            apply()
                        }

                        val userPref = getSharedPreferences("UserPrefs_${user.id}", Context.MODE_PRIVATE)
                        userPref.edit()
                            .putString("username", user.username)
                            .apply()

                        proceedToSetup(user.id)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Email hoặc mật khẩu không chính xác",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // chuyển sang màn hình Đăng ký
        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
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