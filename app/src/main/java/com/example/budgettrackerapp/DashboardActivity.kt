package com.example.budgettrackerapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var pieChart: PieChart
    private lateinit var lvExpensesHome: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup Drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle Back Press using modern API
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // Setup Welcome Text
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val username = prefs.getString("username", "User")
        tvWelcome.text = "Welcome, $username 👋"

        // Update Header Username
        val headerView = navView.getHeaderView(0)
        val tvNavUser = headerView.findViewById<TextView>(R.id.tvNavUser)
        tvNavUser.text = username

        // Initialize UI Elements
        pieChart = findViewById(R.id.pieChart)
        lvExpensesHome = findViewById(R.id.lvExpensesHome)

        setupPieChart()
        loadExpenses()
    }

    private fun setupPieChart() {
        val expensesJson = prefs.getString("expenses", "[]")
        val expensesArray = JSONArray(expensesJson)
        
        val categoryTotals = mutableMapOf<String, Float>()
        
        for (i in 0 until expensesArray.length()) {
            val obj = expensesArray.getJSONObject(i)
            val category = obj.getString("category")
            val amount = obj.getString("amount").toFloatOrNull() ?: 0f
            categoryTotals[category] = categoryTotals.getOrDefault(category, 0f) + amount
        }

        val entries = mutableListOf<PieEntry>()
        for ((category, total) in categoryTotals) {
            entries.add(PieEntry(total, category))
        }

        if (entries.isEmpty()) {
            pieChart.setNoDataText("No expenses logged yet.")
            pieChart.invalidate()
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.centerText = "Spending Categories"
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun loadExpenses() {
        val expensesJson = prefs.getString("expenses", "[]")
        val expensesArray = JSONArray(expensesJson)
        val displayList = mutableListOf<String>()
        
        // Show last 10 expenses
        val start = if (expensesArray.length() > 10) expensesArray.length() - 10 else 0
        for (i in start until expensesArray.length()) {
            val obj = expensesArray.getJSONObject(i)
            displayList.add(
                "R${obj.getString("amount")} — ${obj.getString("description")}\n" +
                "[${obj.getString("category")}] ${obj.getString("date")}"
            )
        }
        displayList.reverse()

        lvExpensesHome.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> { /* Already here */ }
            R.id.nav_expenses -> startActivity(Intent(this, AddExpenseActivity::class.java))
            R.id.nav_categories -> startActivity(Intent(this, CategoryActivity::class.java))
            R.id.nav_budget -> startActivity(Intent(this, BudgetActivity::class.java))
            R.id.nav_reports -> startActivity(Intent(this, ReportsActivity::class.java))
            R.id.nav_achievements -> startActivity(Intent(this, AchievementsActivity::class.java))
            R.id.nav_logout -> {
                prefs.edit().putBoolean("isLoggedIn", false).apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        setupPieChart()
        loadExpenses()
    }
}