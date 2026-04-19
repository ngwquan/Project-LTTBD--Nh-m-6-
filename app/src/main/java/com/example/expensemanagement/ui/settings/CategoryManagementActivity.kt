package com.example.expensemanagement.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryManagementActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var adapter: CategoryAdapter
    private var categoryList = mutableListOf<CategoryEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_management)

        rvCategories = findViewById(R.id.rvCategories)
        fabAddCategory = findViewById(R.id.fabAddCategory)

        setupRecyclerView()
        loadCategories()

        fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(categoryList)
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter

        // Swipe to delete
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                deleteCategory(categoryList[position], position)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(rvCategories)
    }

    private fun loadCategories() {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("current_user_id", -1L)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@CategoryManagementActivity)
            val list = db.categoryDao().getByUser(userId)
            withContext(Dispatchers.Main) {
                categoryList.clear()
                categoryList.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showAddCategoryDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Thêm danh mục mới")
        
        val input = EditText(this)
        input.hint = "Tên danh mục (vd: Ăn uống)"
        builder.setView(input)

        builder.setPositiveButton("Thêm") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                saveCategory(name)
            }
        }
        builder.setNegativeButton("Hủy", null)
        builder.show()
    }

    private fun saveCategory(name: String) {
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getLong("current_user_id", -1L)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@CategoryManagementActivity)
            val newCategory = CategoryEntity(
                userId = userId,
                name = name,
                type = "EXPENSE",
                isSystem = false
            )
            db.categoryDao().insert(newCategory)
            loadCategories() // Refresh list
        }
    }

    private fun deleteCategory(category: CategoryEntity, position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@CategoryManagementActivity)
            db.categoryDao().delete(category)
            withContext(Dispatchers.Main) {
                categoryList.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this@CategoryManagementActivity, "Đã xóa danh mục", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Inner Adapter class for simplicity in one file
    inner class CategoryAdapter(private val items: List<CategoryEntity>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(android.R.id.text1)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvName.text = items[position].name
        }
        override fun getItemCount() = items.size
    }
}
