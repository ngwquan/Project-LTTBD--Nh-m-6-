package com.example.expensemanagement.ui.setup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.databinding.ActivityCategoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var adapter: CategoryAdapter
    private val db by lazy { AppDatabase.getDatabase(this) }

    private var currentUserId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root as View)

        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getLong("current_user_id", -1)

        setupRecyclerView()
        observeCategories()
        checkAndSetupDefaultCategories() // Bổ sung: Tạo dữ liệu mẫu

        // Xử lý Thêm danh mục
        binding.btnAddCategory.setOnClickListener {
            val name = binding.edtCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                insertCategory(name)
                hideKeyboard() // Bổ sung: Ẩn bàn phím cho chuyên nghiệp
            } else {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show()
            }
        }

        // Bổ sung: Nếu bạn có nút quay lại trên thanh tiêu đề
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(emptyList()) { category ->
            deleteCategory(category)
        }
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@CategoryActivity)
            adapter = this@CategoryActivity.adapter
        }
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            db.categoryDao().getAllCategories().collectLatest { list ->
                adapter.updateData(list)
            }
        }
    }

    // Bổ sung: Hàm tự tạo các mục Ăn uống, Đi chơi, Du lịch... nếu chưa có
    private fun checkAndSetupDefaultCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val currentCategories = db.categoryDao().getAllCategories().first()
            if (currentCategories.isEmpty()) {
                val defaults = listOf("Ăn uống", "Đi chơi", "Du lịch", "Mua sắm", "Bạn bè")
                defaults.forEach { name ->
                    db.categoryDao().insert(CategoryEntity(name = name, userId = currentUserId, type = "EXPENSE", isSystem = true))
                }
            }
        }
    }

    private fun insertCategory(name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.categoryDao().insert(CategoryEntity(name = name, userId = currentUserId, type = "EXPENSE", isSystem = false))
            withContext(Dispatchers.Main) {
                binding.edtCategoryName.text.clear()
                Toast.makeText(this@CategoryActivity, "Đã thêm: $name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteCategory(category: CategoryEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.categoryDao().delete(category)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@CategoryActivity, "Đã xóa danh mục", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Bổ sung: Tiện ích ẩn bàn phím
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // Bổ sung: Xử lý nút quay lại của hệ thống
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}