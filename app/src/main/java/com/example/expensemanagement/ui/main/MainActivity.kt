package com.example.expensemanagement.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.expensemanagement.ui.auth.LoginActivity
import com.example.expensemanagement.R
import android.widget.Toast
import android.util.Log
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "Activity chạy", Toast.LENGTH_SHORT).show()

        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        Log.d("DEBUG", "MainActivity started")
//        if (!isLoggedIn) {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//            return
//        }

        setContentView(R.layout.activity_main)
//        val context: Context = this@MainActivity
//        val intent = Intent(context, LoginActivity::class.java)
//        startActivity(intent)
//
//        finish()
    }
}