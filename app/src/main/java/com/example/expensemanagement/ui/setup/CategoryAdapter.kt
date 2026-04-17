package com.example.expensemanagement.ui.setup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.data.local.entity.CategoryEntity
import com.example.expensemanagement.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var categories: List<CategoryEntity>,
    private val onDeleteClick: (CategoryEntity) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root as View)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categories[position]
        holder.binding.tvCategoryName.text = item.name
        holder.binding.btnDeleteCategory.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = categories.size

    fun updateData(newList: List<CategoryEntity>) {
        this.categories = newList
        notifyDataSetChanged()
    }
}