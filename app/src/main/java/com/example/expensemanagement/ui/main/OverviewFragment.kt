package com.example.expensemanagement.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.utils.MoneyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class OverviewFragment : Fragment() {

    private lateinit var txtWelcome: TextView
    private lateinit var txtMoney: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvTotalIncome: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun initViews(view: View) {
        txtWelcome = view.findViewById(R.id.txtWelcome)
        txtMoney = view.findViewById(R.id.txtMoney)
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense)
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
    }

    private fun loadData() {
        val context = requireContext()

        val globalPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)

        if (userId == -1L) {
            startActivity(Intent(context, com.example.expensemanagement.ui.auth.LoginActivity::class.java))
            activity?.finish()
            return
        }

        val userPrefs = context.getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val usernamePref = userPrefs.getString("username", "User")
        val currency = userPrefs.getString("currency", "₫") ?: "₫"

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            val user = db.userDao().getById(userId)
            val transactions = db.transactionDao().getByUser(userId)

            Log.d("Transaction", "Transaction: $transactions")
            val displayName = user?.fullName ?: usernamePref

            // Tính số dư tổng quát
            var totalAllExp = 0.0
            var totalAllInc = 0.0

            // Tính báo cáo tháng này
            var monthExp = 0.0
            var monthInc = 0.0

            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentYear = calendar.get(Calendar.YEAR)

            transactions.forEach { t ->
                // Tính số dư (tất cả thời gian)
                Log.d("Transaction", "Transaction: ${t.amount}")

                if (t.type == "EXPENSE") totalAllExp += t.amount
                else totalAllInc += t.amount

                // Lọc cho tháng này
                calendar.timeInMillis = t.transactionDate
                if (calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear) {
                    if (t.type == "EXPENSE") monthExp += t.amount
                    else monthInc += t.amount
                }
            }
            Log.d("Transaction", "tiền chi: $totalAllExp")

            val balance = (totalAllInc - totalAllExp).toLong()

            withContext(Dispatchers.Main) {

                txtWelcome.text = "Xin chào, $displayName"
                txtMoney.text = MoneyUtils.format(balance.toString(), currency)
                tvTotalExpense.text = MoneyUtils.format(monthExp.toLong().toString(), currency)
                tvTotalIncome.text = MoneyUtils.format(monthInc.toLong().toString(), currency)

                userPrefs.edit()
                    .putString("money", balance.toString())
                    .putString("username", displayName)
                    .apply()
            }
        }
    }
}
