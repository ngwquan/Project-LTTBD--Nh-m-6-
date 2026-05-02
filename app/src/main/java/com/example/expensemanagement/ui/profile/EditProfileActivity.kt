package com.example.expensemanagement.ui.profile

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.UserEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etOldPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private var currentUser: UserEntity? = null
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setupToolbar()
        initViews()
        loadUserData()
        setupListeners()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etOldPassword = findViewById(R.id.etOldPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun loadUserData() {
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        userId = globalPref.getLong("current_user_id", -1)

        if (userId == -1L) {
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@EditProfileActivity)
            currentUser = db.userDao().getById(userId)

            withContext(Dispatchers.Main) {
                currentUser?.let {
                    etFullName.setText(it.fullName)
                    etEmail.setText(it.email)
                }
            }
        }
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val oldPassword = etOldPassword.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ họ tên và email", Toast.LENGTH_SHORT).show()
            return
        }

        val user = currentUser ?: return

        // Logic đổi mật khẩu đã được khôi phục mã hóa băm
        var updatedPasswordHash = user.passwordHash
        if (newPassword.isNotEmpty()) {
            val hashedOldPassword = hashPassword(oldPassword)
            if (hashedOldPassword != user.passwordHash) {
                Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show()
                return
            }
            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                return
            }
            if (newPassword.length < 8) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show()
                return
            }
            updatedPasswordHash = hashPassword(newPassword)
        }

        val updatedUser = user.copy(
            id = user.id,
            fullName = fullName,
            email = email,
            passwordHash = updatedPasswordHash
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@EditProfileActivity)
            db.userDao().update(updatedUser)

            // Đồng bộ lại UI trong SharedPreferences
            val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
            userPrefs.edit()
                .putString("username", fullName)
                .apply()

            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditProfileActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
