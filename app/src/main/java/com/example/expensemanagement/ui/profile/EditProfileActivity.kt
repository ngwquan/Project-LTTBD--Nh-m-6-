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
        val oldPassword = etOldPassword.text.toString()
        val newPassword = etNewPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ họ tên và email", Toast.LENGTH_SHORT).show()
            return
        }

        val user = currentUser ?: return

        // Password change logic
        var updatedPasswordHash = user.passwordHash
        if (newPassword.isNotEmpty()) {
            if (oldPassword != user.passwordHash) { // Simplified password check
                Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show()
                return
            }
            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                return
            }
            if (newPassword.length < 6) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return
            }
            updatedPasswordHash = newPassword
        }

        val updatedUser = user.copy(
            fullName = fullName,
            email = email,
            passwordHash = updatedPasswordHash
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@EditProfileActivity)
            db.userDao().insert(updatedUser)

            // Sync with SharedPreferences (similar to ProfileActivity)
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
}
