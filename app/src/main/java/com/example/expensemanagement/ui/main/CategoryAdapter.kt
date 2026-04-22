package com.example.expensemanagement.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.entity.CategoryEntity

class CategoryAdapter(
    private var categories: List<CategoryEntity>,
    private val onCategorySelected: (CategoryEntity) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = -1

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val iconView: View = view.findViewById(R.id.viewCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name
        
        if (position == selectedPosition) {
            holder.iconView.background.setTint(Color.parseColor("#4DB6E2"))
            holder.tvName.setTextColor(Color.parseColor("#4DB6E2"))
        } else {
            holder.iconView.background.setTint(Color.parseColor("#E0E0E0"))
            holder.tvName.setTextColor(Color.parseColor("#333333"))
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onCategorySelected(category)
        }
    }

    override fun getItemCount() = categories.size

    fun updateData(newCategories: List<CategoryEntity>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    fun setSelectedCategory(categoryId: Long) {
        selectedPosition = categories.indexOfFirst { it.id == categoryId }
        notifyDataSetChanged()
    }
}
