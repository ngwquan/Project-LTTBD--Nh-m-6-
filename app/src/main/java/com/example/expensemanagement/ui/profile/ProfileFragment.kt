package com.example.expensemanagement.ui.profile

import android.content.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.expensemanagement.R
import com.example.expensemanagement.data.local.database.AppDatabase
import com.example.expensemanagement.ui.auth.LoginActivity
import com.example.expensemanagement.utils.MoneyUtils
import kotlinx.coroutines.*

class ProfileFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvUserBalance: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews(view)
        setupUI(view)
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun initViews(view: View) {
        tvUsername = view.findViewById(R.id.tvUsername)
        tvUserBalance = view.findViewById(R.id.tvUserBalance)
    }

    private fun setupUI(view: View) {
        view.findViewById<View>(R.id.btnEditProfile).setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<View>(R.id.btnManageCategories).setOnClickListener {
            Toast.makeText(requireContext(), "Tính năng quản lý danh mục", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.btnLogout).setOnClickListener {
            logout()
        }
    }

    private fun loadUserData() {
        val context = requireContext()

        // Sử dụng cùng logic với MainActivity để lấy userId
        val globalPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = globalPref.getLong("current_user_id", -1)

        if (userId == -1L) {
            startActivity(Intent(context, LoginActivity::class.java))
            requireActivity().finish()
            return
        }

        val userPrefs = context.getSharedPreferences("UserPrefs_$userId", Context.MODE_PRIVATE)
        val usernamePref = userPrefs.getString("username", "User")
        val currency = userPrefs.getString("currency", "₫") ?: "₫"

        tvUsername.text = usernamePref

        // Tính toán số dư từ Database để đảm bảo chính xác tuyệt đối
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context)
            val user = db.userDao().getById(userId)
            val transactions = db.transactionDao().getByUser(userId)

            val displayName = user?.fullName ?: usernamePref

            var totalExp = 0.0
            var totalInc = 0.0

            for (t in transactions) {
                if (t.type == "EXPENSE") totalExp += t.amount
                else totalInc += t.amount
            }

            val balance = totalInc - totalExp

            withContext(Dispatchers.Main) {
                // Sử dụng MoneyUtils để định dạng tiền giống trang Main
                tvUsername.text = displayName
                tvUserBalance.text = "Số dư: ${MoneyUtils.format(balance.toLong().toString(), currency)}"

                // Cập nhật lại SharedPreferences để đồng bộ
                userPrefs.edit()
                    .putString("money", balance.toLong().toString())
                    .putString("username", displayName)
                    .apply()
            }
        }
    }
    private fun logout() {
        val context = requireContext()

        val globalPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        globalPref.edit().remove("current_user_id").apply()

        Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
