package com.example.expensemanagement.ui.profile

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
    private lateinit var spinnerCurrency: Spinner
    private lateinit var btnSave: MaterialButton
    private var currentUser: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)
        btnSave = findViewById(R.id.btnSave)

        setupCurrencySpinner()
        loadUserData()

        btnSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun setupCurrencySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency.adapter = adapter
    }

    private fun loadUserData() {
        val userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = userPrefs.getLong("current_user_id", -1L)
        
        if (userId == -1L) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch(Dispatchers.IO) {
            currentUser = db.userDao().getById(userId)
            withContext(Dispatchers.Main) {
                currentUser?.let { user ->
                    etFullName.setText(user.fullName)
                    etEmail.setText(user.email)
                    
                    val currencies = resources.getStringArray(R.array.currency_list)
                    val index = currencies.indexOfFirst { it.startsWith(user.currency) }
                    if (index >= 0) {
                        spinnerCurrency.setSelection(index)
                    }
                }
            }
        }
    }

    private fun saveChanges() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val selectedCurrency = spinnerCurrency.selectedItem.toString()
        
        // Extract currency code (e.g., "VND" from "VND - Việt Nam Đồng")
        val currencyCode = selectedCurrency.split(" ")[0]

        if (fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser?.let { user ->
            val updatedUser = user.copy(
                fullName = fullName,
                email = email,
                currency = currencyCode
            )

            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch(Dispatchers.IO) {
                db.userDao().insert(updatedUser) // Using insert as it has OnConflictStrategy.REPLACE
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
