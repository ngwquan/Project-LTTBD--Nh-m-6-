package com.example.expensemanagement.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class ResetPasswordActivity : AppCompatActivity() {

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_reset_password)

        val edtAccount = findViewById<TextInputEditText>(R.id.edtAccount)
                val edtNewPass = findViewById<TextInputEditText>(R.id.edtNewPassword)
                val edtConfirm = findViewById<TextInputEditText>(R.id.edtConfirmPassword)
                val btnSave = findViewById<MaterialButton>(R.id.btnSave)

                val db = AppDatabase.Companion.getDatabase(this)
        val userDao = db.userDao()

        btnSave.setOnClickListener {
            val account = edtAccount.text.toString().trim()
            val newPass = edtNewPass.text.toString().trim()
            val confirm = edtConfirm.text.toString().trim()

            val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

            // Validate
            if (account.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!passwordRegex.matches(newPass)) {
                edtNewPass.error = "Mật khẩu ≥8 ký tự, có chữ hoa, thường và số"
                return@setOnClickListener
            }

            if (newPass != confirm) {
                edtConfirm.error = "Mật khẩu không khớp"
                return@setOnClickListener
            }

            // Update mật khẩu
            lifecycleScope.launch(Dispatchers.IO) {

                // tìm user theo email hoặc username
                val user = userDao.getUserByEmailOrUsername(account)

                if (user != null) {
                    val hashed = hashPassword(newPass)
                    userDao.updatePasswordById(user.id, hashed)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Đổi mật khẩu thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Tài khoản không tồn tại",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}