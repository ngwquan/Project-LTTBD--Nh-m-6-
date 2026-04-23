package com.example.expensemanagement.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.entity.CategoryEntity

class CategoryAdapter(
    private var categories: List<CategoryEntity>,
    private val onCategorySelected: (CategoryEntity) -> Unit // Callback để báo về Activity
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = -1

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val iconView: View = view.findViewById(R.id.viewCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name

        // Gán màu sắc động dựa trên tên danh mục
        val categoryColor = getCategoryColor(category.name)

        if (position == selectedPosition) {
            // Hiệu ứng khi được CLICK chọn: Sáng lên và đậm chữ
            holder.iconView.background.setTint(Color.parseColor(categoryColor))
            holder.iconView.alpha = 1.0f
            holder.tvName.setTextColor(Color.parseColor("#4DB6E2"))
            holder.tvName.paint.isFakeBoldText = true
        } else {
            // Hiệu ứng khi chưa chọn: Mờ đi (Alpha 0.3) để làm nổi bật mục đã chọn
            holder.iconView.background.setTint(Color.parseColor(categoryColor))
            holder.iconView.alpha = 0.3f
            holder.tvName.setTextColor(Color.parseColor("#757575"))
            holder.tvName.paint.isFakeBoldText = false
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition

            // Cập nhật lại giao diện cho mục cũ và mục mới
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)

            // TRUYỀN DỮ LIỆU ĐỘNG: Báo cho Activity biết danh mục nào vừa được chọn
            onCategorySelected(category)
        }
    }

    private fun getCategoryColor(name: String): String {
        return when (name) {
            "Ăn uống" -> "#FFA500"
            "Đi lại" -> "#A52A2A"
            "Mua sắm" -> "#0000FF"
            "Y tế" -> "#00FF7F"
            "Giáo dục" -> "#FF4500"
            "Tiền điện" -> "#00BFFF"
            "Mỹ phẩm" -> "#FF69B4"
            "Lương" -> "#4CAF50"
            "Số dư đầu" -> "#4DB6E2"
            "Chi tiêu khác" -> "#9E9E9E"
            "Thu nhập khác" -> "#8BC34A"
            else -> "#757575"
        }
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<CategoryEntity>) {
        this.categories = newCategories
        this.selectedPosition = -1 // Reset khi chuyển Tab
        notifyDataSetChanged()
    }
}