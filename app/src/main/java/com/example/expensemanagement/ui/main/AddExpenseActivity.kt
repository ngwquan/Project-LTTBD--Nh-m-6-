package com.example.expensemanagement.ui.main

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.data.local.entity.WalletEntity
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

    private lateinit var tvDate: TextView

    private lateinit var btnSelectDate: LinearLayout
    private var selectedCategory: CategoryEntity? = null
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        initViews()
        setupRecyclerView()
        initDefaultDataAndLoad()

        btnBack.setOnClickListener { finish() }
        tvTabExpense.setOnClickListener { switchType(true) }
        tvTabIncome.setOnClickListener { switchType(false) }

        switchType(true)
        setupAmountFormatting()

        updateDateText(selectedDateMillis)
        btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            handleSaveAction()
        }
    }

    private fun initViews() {
        tvTabExpense = findViewById(R.id.tvTabExpense)
        tvTabIncome = findViewById(R.id.tvTabIncome)
        tvAmountLabel = findViewById(R.id.tvAmountLabel)
        edtAmount = findViewById(R.id.edtAmount)
        edtNote = findViewById(R.id.edtNote)
        btnSave = findViewById(R.id.btnSaveExpense)
        btnBack = findViewById(R.id.btnBack)
        rvCategories = findViewById(R.id.rvCategories)
        tvDate = findViewById(R.id.tvDate)
        btnSelectDate = findViewById(R.id.btnSelectDate)
    }

    private fun showDatePicker() {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = selectedDateMillis

        val datePicker = android.app.DatePickerDialog(
            this,
            { _, year, month, day ->
                val selectedCal = java.util.Calendar.getInstance()
                selectedCal.set(year, month, day, 0, 0, 0)

                selectedDateMillis = selectedCal.timeInMillis
                updateDateText(selectedDateMillis)
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun updateDateText(timeMillis: Long) {
        val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("vi", "VN"))
        tvDate.text = format.format(java.util.Date(timeMillis))
    }

    private fun setupRecyclerView() {
        rvCategories.layoutManager = GridLayoutManager(this, 3)
        categoryAdapter = CategoryAdapter(emptyList()) { category ->
            selectedCategory = category
        }
        rvCategories.adapter = categoryAdapter
    }

    private fun initDefaultDataAndLoad() {
        lifecycleScope.launch(Dispatchers.IO) {
            val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = globalPref.getLong("current_user_id", -1)
            if (userId == -1L) return@launch

            val db = AppDatabase.getDatabase(this@AddExpenseActivity)
            val categoryDao = db.categoryDao()

            if (categoryDao.getByUser(userId).isEmpty()) {
                val defaultList = listOf(
                    CategoryEntity(userId = userId, name = "Ăn uống", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Đi lại", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Mua sắm", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Y tế", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Giáo dục", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Tiền điện", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Chi tiêu khác", type = "EXPENSE", isSystem = false),
                    CategoryEntity(userId = userId, name = "Lương", type = "INCOME", isSystem = false),
                    CategoryEntity(userId = userId, name = "Số dư đầu", type = "INCOME", isSystem = true),
                    CategoryEntity(userId = userId, name = "Thu nhập khác", type = "INCOME", isSystem = false)
                )
                defaultList.forEach { categoryDao.insert(it) }
            }

            withContext(Dispatchers.Main) {
                loadCategoriesFromDb()
            }
        }
    }

    private fun switchType(expense: Boolean) {
        isExpense = expense
        selectedCategory = null
        if (isExpense) {
            tvTabExpense.setBackgroundResource(R.drawable.bg_toggle_selected)
            tvTabExpense.setTextColor(android.graphics.Color.WHITE)
            tvTabIncome.setBackgroundResource(0)
            tvTabIncome.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
            tvAmountLabel.text = "Số tiền chi"
            btnSave.text = "LƯU KHOẢN CHI"
        } else {
            tvTabIncome.setBackgroundResource(R.drawable.bg_toggle_selected)
            tvTabIncome.setTextColor(android.graphics.Color.WHITE)
            tvTabExpense.setBackgroundResource(0)
            tvTabExpense.setTextColor(android.graphics.Color.parseColor("#A0A0A0"))
            tvAmountLabel.text = "Số tiền thu"
            btnSave.text = "LƯU KHOẢN THU"
        }
        loadCategoriesFromDb()
    }

    private fun loadCategoriesFromDb() {
        val type = if (isExpense) "EXPENSE" else "INCOME"
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AddExpenseActivity)
            val list = db.categoryDao().getByUser(userId).filter { it.type == type }
            withContext(Dispatchers.Main) {
                categoryAdapter.updateData(list)
            }
        }
    }

    private fun handleSaveAction() {
        val amountStr = edtAmount.text.toString().replace(".", "").replace(",", "")
        val amount = amountStr.toDoubleOrNull() ?: 0.0

        if (amount <= 0) {
            Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn một danh mục", Toast.LENGTH_SHORT).show()
            return
        }

        saveToDatabase(amount)
    }

    private fun saveToDatabase(amount: Double) {
        val globalPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@AddExpenseActivity)
            var wallet = db.walletDao().getByUser(userId).firstOrNull()

            if (wallet == null) {
                db.walletDao().insert(WalletEntity(userId = userId, name = "Ví chính", type = "CASH", balance = 0.0, isDefault = true))
                wallet = db.walletDao().getByUser(userId).first()
            }

            val transaction = TransactionEntity(
                userId = userId,
                walletId = wallet.id,
                categoryId = selectedCategory!!.id,
                amount = amount,
                type = if (isExpense) "EXPENSE" else "INCOME",
                note = edtNote.text.toString(),
                transactionDate = selectedDateMillis
            )
            db.transactionDao().insert(transaction)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddExpenseActivity, "Đã lưu thành công!", Toast.LENGTH_SHORT).show()
                finish()
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
                        val formatted = java.text.NumberFormat.getInstance(java.util.Locale("vi", "VN")).format(cleanString.toLong())
                        current = formatted
                        edtAmount.setText(formatted)
                        edtAmount.setSelection(formatted.length)
                    }
                    edtAmount.addTextChangedListener(this)
                }
            }
        })
    }
}