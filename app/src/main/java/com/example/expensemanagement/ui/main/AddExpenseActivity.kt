package com.example.expensemanagement.ui.main

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.utils.MoneyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddExpenseActivity : AppCompatActivity() {

    private var isExpense = true
    private lateinit var tvTabExpense: TextView
    private lateinit var tvTabIncome: TextView
    private lateinit var tvAmountLabel: TextView
    private lateinit var edtAmount: EditText
    private lateinit var edtNote: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView
    private lateinit var rvCategories: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private var selectedCategory: com.example.expensemanagement.data.local.entity.CategoryEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        tvTabExpense = findViewById(R.id.tvTabExpense)
        tvTabIncome = findViewById(R.id.tvTabIncome)
        tvAmountLabel = findViewById(R.id.tvAmountLabel)
        edtAmount = findViewById(R.id.edtAmount)
        edtNote = findViewById(R.id.edtNote)
        btnSave = findViewById(R.id.btnSaveExpense)
        btnBack = findViewById(R.id.btnBack)
        rvCategories = findViewById(R.id.rvCategories)

        setupCategories()

        btnBack.setOnClickListener { finish() }

        tvTabExpense.setOnClickListener { switchType(true) }
        tvTabIncome.setOnClickListener { switchType(false) }

        switchType(true)

        setupAmountFormatting()

        btnSave.setOnClickListener {
            val amountStr = edtAmount.text.toString()
            if (amountStr.isEmpty() || amountStr == "0") {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Xử lý cả trường hợp người dùng nhập có dấu chấm/phẩy
            val cleanAmount = amountStr.replace(".", "").replace(",", "")
            val amount = cleanAmount.toDoubleOrNull() ?: 0.0
            val note = edtNote.text.toString()
            val type = if (isExpense) "EXPENSE" else "INCOME"

            saveTransaction(amount, note, type)
        }
    }

    private fun switchType(expense: Boolean) {
        isExpense = expense
        if (isExpense) {
            tvTabExpense.setBackgroundResource(R.drawable.bg_toggle_selected)
            tvTabExpense.setTextColor(android.graphics.Color.WHITE)
            tvTabIncome.setBackgroundResource(0)
            tvTabIncome.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
            tvAmountLabel.text = "Số tiền chi"
            btnSave.text = "Lưu chi tiêu"
        } else {
            tvTabIncome.setBackgroundResource(R.drawable.bg_toggle_selected)
            tvTabIncome.setTextColor(android.graphics.Color.WHITE)
            tvTabExpense.setBackgroundResource(0)
            tvTabExpense.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
            tvAmountLabel.text = "Số tiền thu"
            btnSave.text = "Lưu thu nhập"
        }
        loadCategoriesForType(if (isExpense) "EXPENSE" else "INCOME")
    }

    private fun setupCategories() {
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            selectedCategory = category
        }
        rvCategories.adapter = categoryAdapter
    }

    private fun loadCategoriesForType(type: String) {
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)
        if (userId == -1L) return

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AddExpenseActivity)
            val categories = db.categoryDao().getByUser(userId).filter { it.type == type }
            
            withContext(Dispatchers.Main) {
                categoryAdapter.updateData(categories)
                selectedCategory = null // Reset selection when switching type
            }
        }
    }

    private fun setupAmountFormatting() {
        edtAmount.addTextChangedListener(object : android.text.TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (s.toString() != current) {
                    edtAmount.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^0-9]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val formatted = try {
                            val parsed = cleanString.toLong()
                            java.text.NumberFormat.getInstance(java.util.Locale("vi", "VN")).format(parsed)
                        } catch (e: Exception) {
                            ""
                        }
                        current = formatted
                        edtAmount.setText(formatted)
                        edtAmount.setSelection(formatted.length)
                    } else {
                        current = ""
                        edtAmount.setText("")
                    }

                    edtAmount.addTextChangedListener(this)
                }
            }
        })
    }

    private fun saveTransaction(amount: Double, note: String, type: String) {
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)
        if (userId == -1L) return

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AddExpenseActivity)
            
            // 0. Đảm bảo có Wallet và Category mặc định để tránh lỗi khóa ngoại
            val walletDao = db.walletDao()
            val categoryDao = db.categoryDao()
            
            var defaultWallet = walletDao.getByUser(userId).firstOrNull()
            if (defaultWallet == null) {
                walletDao.insert(com.example.expensemanagement.data.local.entity.WalletEntity(
                    userId = userId,
                    name = "Ví chính",
                    type = "CASH",
                    balance = 0.0,
                    isDefault = true
                ))
                defaultWallet = walletDao.getByUser(userId).first()
            }

            var defaultCategory = selectedCategory ?: categoryDao.getByUser(userId).firstOrNull { it.type == type }
            if (defaultCategory == null) {
                categoryDao.insert(com.example.expensemanagement.data.local.entity.CategoryEntity(
                    userId = userId,
                    name = if (type == "EXPENSE") "Chi tiêu khác" else "Thu nhập khác",
                    type = type,
                    isSystem = true
                ))
                defaultCategory = categoryDao.getByUser(userId).first { it.type == type }
            }

            // 1. Lưu vào Database
            val transaction = TransactionEntity(
                userId = userId,
                walletId = defaultWallet.id, 
                categoryId = defaultCategory.id, 
                amount = amount,
                type = type,
                note = note,
                transactionDate = System.currentTimeMillis()
            )
            db.transactionDao().insert(transaction)

            // 2. Cập nhật số dư trong SharedPreferences
            val userPrefs = getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
            val currentMoneyStr = userPrefs.getString("money", "0")?.replace("[^0-9]".toRegex(), "") ?: "0"
            val currentMoney = currentMoneyStr.toLongOrNull() ?: 0
            
            val newMoney = if (type == "EXPENSE") {
                currentMoney - amount.toLong()
            } else {
                currentMoney + amount.toLong()
            }

            userPrefs.edit()
                .putString("money", newMoney.toString())
                .commit() 

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddExpenseActivity, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}