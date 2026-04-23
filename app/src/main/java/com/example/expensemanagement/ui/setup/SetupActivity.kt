package com.example.expensemanagement.ui.setup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.data.local.entity.WalletEntity
import com.example.expensemanagement.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val txtWelcome = findViewById<TextView>(R.id.txtWelcome)
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)
        val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)

        val username = userPrefs.getString("username", "User")
        val spinner = findViewById<Spinner>(R.id.spinnerCurrency)
        val edtMoney = findViewById<EditText>(R.id.edtMoney)
        val btnSave = findViewById<Button>(R.id.btnSave)

        txtWelcome.text = "Xin chào, $username"

        // Tự động định dạng số tiền khi nhập
        edtMoney.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    edtMoney.removeTextChangedListener(this)

                    val cleanString = s.toString().replace(".", "")

                    if (cleanString.isNotEmpty()) {
                        try {
                            val parsed = cleanString.toDouble()
                            val formatter = NumberFormat.getInstance(Locale("vi", "VN")) as DecimalFormat
                            formatter.applyPattern("#,###")
                            val formatted = formatter.format(parsed).replace(",", ".")

                            current = formatted
                            edtMoney.setText(formatted)
                            edtMoney.setSelection(formatted.length)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        current = ""
                    }

                    edtMoney.addTextChangedListener(this)
                }
            }
        })

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.currency_list,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)

        btnSave.setOnClickListener {
            val moneyFormatted = edtMoney.text.toString()
            if (moneyFormatted.isEmpty()) {
                edtMoney.error = "Vui lòng nhập số tiền"
                return@setOnClickListener
            }

            val rawMoney = moneyFormatted.replace(".", "")
            val amount = rawMoney.toLong()
            val currency = spinner.selectedItem.toString()

            // Lưu dữ liệu vào SharedPreferences của User hiện tại
            userPrefs.edit()
                .putString("money", rawMoney)
                .putLong("initial_balance", amount)
                .putString("currency", currency)
                .putBoolean("isSetupDone", true) // Đánh dấu đã setup xong
                .commit() // Dùng commit để ghi ngay lập tức
            val amount = rawMoney.toDoubleOrNull() ?: 0.0
            val currency = spinner.selectedItem.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@SetupActivity)
                
                // 1. Tạo ví mặc định nếu chưa có
                var defaultWallet = db.walletDao().getByUser(userId).firstOrNull()
                if (defaultWallet == null) {
                    db.walletDao().insert(WalletEntity(
                        userId = userId,
                        name = "Ví chính",
                        type = "CASH",
                        balance = 0.0,
                        isDefault = true
                    ))
                    defaultWallet = db.walletDao().getByUser(userId).first()
                }

                // 2. Tạo danh mục mặc định
                initDefaultCategories(db, userId)

                // 3. Tạo giao dịch thu nhập ban đầu nếu số tiền > 0
                if (amount > 0) {
                    val initialCat = db.categoryDao().getByUser(userId).firstOrNull { it.name == "Số dư đầu" }
                    if (initialCat != null) {
                        db.transactionDao().insert(TransactionEntity(
                            userId = userId,
                            walletId = defaultWallet.id,
                            categoryId = initialCat.id,
                            amount = amount,
                            type = "INCOME",
                            note = "Số dư thiết lập ban đầu",
                            transactionDate = System.currentTimeMillis()
                        ))
                    }
                }

                withContext(Dispatchers.Main) {
                    userPrefs.edit()
                        .putString("money", rawMoney)
                        .putString("currency", currency)
                        .putBoolean("isSetupDone", true)
                        .apply()

                    Toast.makeText(this@SetupActivity, "Thiết lập thành công!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@SetupActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private suspend fun initDefaultCategories(db: AppDatabase, userId: Long) {
        val categoryDao = db.categoryDao()
        val existing = categoryDao.getByUser(userId)
        if (existing.isNotEmpty()) return

        val defaults = listOf(
            // Chi tiêu
            CategoryEntity(userId = userId, name = "Ăn uống", type = "EXPENSE", isSystem = false),
            CategoryEntity(userId = userId, name = "Đi lại", type = "EXPENSE", isSystem = false),
            CategoryEntity(userId = userId, name = "Mua sắm", type = "EXPENSE", isSystem = false),
            CategoryEntity(userId = userId, name = "Y tế", type = "EXPENSE", isSystem = false),
            CategoryEntity(userId = userId, name = "Giáo dục", type = "EXPENSE", isSystem = false),
            CategoryEntity(userId = userId, name = "Tiền điện", type = "EXPENSE", isSystem = false),
            CategoryEntity(userId = userId, name = "Chi tiêu khác", type = "EXPENSE", isSystem = false),
            // Thu nhập
            CategoryEntity(userId = userId, name = "Lương", type = "INCOME", isSystem = false),
            CategoryEntity(userId = userId, name = "Số dư đầu", type = "INCOME", isSystem = true),
            CategoryEntity(userId = userId, name = "Thu nhập khác", type = "INCOME", isSystem = false)
        )

        defaults.forEach { categoryDao.insert(it) }
    }
}
