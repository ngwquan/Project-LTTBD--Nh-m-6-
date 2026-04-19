package com.example.expensemanagement.ui.main

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.TransactionEntity
import com.example.expensemanagement.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private var currentAmountString = "0"
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupKeypad()
        setupDatePicker()
        setupSaveButton()
    }

    private fun setupUI() {
        updateAmountDisplay()
        updateDateDisplay()
    }

    private fun setupKeypad() {
        // Danh sách các nút số từ 0-9
        val numButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        numButtons.forEach { button ->
            button.setOnClickListener {
                val digit = button.text.toString()
                if (currentAmountString == "0") {
                    currentAmountString = digit
                } else {
                    currentAmountString += digit
                }
                updateAmountDisplay()
            }
        }

        // Nút xóa (Clear)
        binding.btnC.setOnClickListener {
            currentAmountString = "0"
            updateAmountDisplay()
        }
    }

    private fun updateAmountDisplay() {
        binding.tvAmountDisplay.text = currentAmountString
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDate
            
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)
                    selectedDate = selectedCalendar.timeInMillis
                    updateDateDisplay()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateDisplay() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date(selectedDate))
    }

    private fun setupSaveButton() {
        binding.btnSaveExpense.setOnClickListener {
            val amount = currentAmountString.toDoubleOrNull() ?: 0.0
            val note = binding.edtNote.text.toString().trim()

            // 1. Kiểm tra số tiền
            if (amount <= 0) {
                Toast.makeText(this, "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Lấy userId từ SharedPreferences
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val userId = sharedPref.getLong("current_user_id", -1L)

            if (userId == -1L) {
                Toast.makeText(this, "Lỗi: Không tìm thấy phiên đăng nhập", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Thực hiện lưu vào database
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val db = AppDatabase.getDatabase(this@AddExpenseActivity)
                    
                    // Lấy ví (Wallet) đầu tiên của user để gán vào giao dịch
                    val wallets = db.walletDao().getByUser(userId)
                    if (wallets.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddExpenseActivity, "Vui lòng tạo ví trong ứng dụng trước", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    val walletId = wallets[0].id

                    // Lấy danh mục (Category) đầu tiên của user
                    val categories = db.categoryDao().getByUser(userId)
                    if (categories.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddExpenseActivity, "Vui lòng tạo danh mục chi tiêu trước", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    val categoryId = categories[0].id

                    // Tạo đối tượng TransactionEntity (Thành viên 2 không sửa file Entity)
                    val transaction = TransactionEntity(
                        userId = userId,
                        walletId = walletId,
                        categoryId = categoryId,
                        amount = amount,
                        type = "EXPENSE",
                        note = note,
                        transactionDate = selectedDate
                    )

                    // Gọi Dao để lưu
                    db.transactionDao().insert(transaction)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddExpenseActivity, "Lưu chi tiêu thành công!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Trả về kết quả cho MainActivity để reload data
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddExpenseActivity, "Lỗi hệ thống: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
