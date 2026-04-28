package com.example.expensemanagement.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.model.TransactionWithCategory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var transactions: List<TransactionWithCategory>,
    private var onitemclick: (TransactionWithCategory) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    // LỚP VIEW HOLDER: Ánh xạ chính xác các ID từ item_transaction.xml
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val viewCategoryColor: View = view.findViewById(R.id.viewCategoryColor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]

        // 1. Hiển thị Tên danh mục (Động từ Database)
        holder.tvCategoryName.text = item.categoryName

        // 2. Hiển thị Ngày tháng (Định dạng dd/MM/yyyy)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(item.date))

        // 3. Định dạng số tiền VNĐ và màu sắc (Đỏ cho Chi, Xanh cho Thu)
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        if (item.type == "EXPENSE") {
            holder.tvAmount.text = "- ${formatter.format(item.amount)} đ"
            holder.tvAmount.setTextColor(Color.parseColor("#F44336")) // Màu đỏ
        } else {
            holder.tvAmount.text = "+ ${formatter.format(item.amount)} đ"
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50")) // Màu xanh lá
        }

        // 4. Đổ màu Pastel động (Đồng bộ hoàn toàn với bảng màu bạn đã chọn)
        val displayColor = when (item.categoryName) {
            "Ăn uống"   -> "#F9E2B0" // Vàng nhạt
            "Di lại"    -> "#DBC0C0" // Hồng xám
            "Mua sắm"   -> "#B0B5F9" // Tím xanh nhạt
            "Y tế"      -> "#B0F9D9" // Xanh bạc hà
            "Giáo dục"  -> "#F9CBB0" // Cam nhạt
            "Tiền điện" -> "#B0E9F9" // Xanh dương nhạt
            "Mỹ phẩm"   -> "#F9D0E6" // Hồng phấn
            "Lương"     -> "#C8E6C9" // Xanh lá nhạt
            "Tiền thưởng" -> "#FFF9C4" // Vàng chanh
            else        -> "#E0E0E0" // Màu xám mặc định
        }
        // 5. Bắt sự kiện click vào item để mở chi tiết
        holder.itemView.setOnClickListener { onitemclick(item) }


        // Cập nhật màu sắc cho vòng tròn icon
        holder.viewCategoryColor.background?.setTint(Color.parseColor(displayColor))
    }

    override fun getItemCount() = transactions.size

    // Hàm cập nhật dữ liệu động khi Database thay đổi
    fun updateData(newList: List<TransactionWithCategory>) {
        transactions = newList
        notifyDataSetChanged()
    }
}