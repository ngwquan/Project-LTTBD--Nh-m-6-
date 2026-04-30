package com.example.expensemanagement.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.expensemanagement.R
import com.example.expensemanagement.ui.analytics.AnalyticsFragment
import com.example.expensemanagement.ui.history.HistoryFragment
import com.example.expensemanagement.ui.profile.ProfileFragment
import java.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton
    private val overviewFragment = OverviewFragment()
    private val historyFragment = HistoryFragment()
    private val analyticsFragment = AnalyticsFragment()
    private val profileFragment = ProfileFragment()

    private var activeFragment: Fragment = overviewFragment



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigationView)
        fabAdd = findViewById(R.id.fabAdd)

        setupUI()
        setupFragment(savedInstanceState)
        setupNavigation()
    }

    // Khởi tạo giao diện
    private fun setupUI() {
        bottomNav.background = null
        bottomNav.menu.getItem(2).isEnabled = false

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    // Khởi tạo fragment
    private fun setupFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, overviewFragment, "overview")
                .add(R.id.fragment_container, historyFragment, "history").hide(historyFragment)
                .add(R.id.fragment_container, analyticsFragment, "analytics").hide(analyticsFragment)
                .add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment)
                .commit()

            activeFragment = overviewFragment
        } else {
            activeFragment = supportFragmentManager.findFragmentByTag("overview") ?: overviewFragment
        }

        }

    // Khởi tạo thanh điều hướng
    private fun setupNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_overview -> switchFragment(overviewFragment)
                R.id.nav_history -> switchFragment(historyFragment)
                R.id.nav_profile -> switchFragment(profileFragment)
                R.id.nav_statistics -> switchFragment(analyticsFragment)
            }
            true
        }
        bottomNav.setOnItemReselectedListener { }
    }

    private fun switchFragment(target : Fragment) {
        if (target == activeFragment) return

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .hide(activeFragment)
            .show(target)
            .commit()

        activeFragment = target
    }

}