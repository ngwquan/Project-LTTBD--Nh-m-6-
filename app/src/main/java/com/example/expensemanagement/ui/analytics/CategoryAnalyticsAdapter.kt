package com.example.expensemanagement.ui.analytics

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.utils.MoneyUtils

data class CategorySummary(
    val name: String,
    val amount: Double,
    val color: String,
    val percentage: Float
)

class CategoryAnalyticsAdapter(private var items: List<CategorySummary>) :
    RecyclerView.Adapter<CategoryAnalyticsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val viewIcon: View = view.findViewById(R.id.viewCategoryColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Sử dụng lại item_category nhưng tùy chỉnh hiển thị
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvAmount.text = MoneyUtils.format(item.amount.toLong().toString(), "₫")
        
        // Hiển thị màu sắc danh mục
        holder.viewIcon.background.setTint(Color.parseColor(item.color))
        
        // Nếu là chi tiêu thì để màu đỏ, thu nhập màu xanh (tùy chọn)
        if (item.amount < 0) {
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"))
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<CategorySummary>) {
        items = newItems
        notifyDataSetChanged()
    }
}
