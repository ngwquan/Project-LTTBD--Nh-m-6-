package com.example.expensemanagement.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var transactions: List<TransactionEntity>,
    private val onItemClick: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvNote.text = transaction.note ?: "Không có ghi chú"
        holder.tvAmount.text = String.format("%,.0f đ", transaction.amount)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(transaction.transactionDate))

        holder.itemView.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
