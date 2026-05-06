package com.example.budgettrackerapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

// Screen to set and view monthly budget limits per category
class BudgetActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        val spinnerCat  = findViewById<Spinner>(R.id.spinnerCategory)
        val etLimit     = findViewById<EditText>(R.id.etBudgetLimit)
        val btnSave     = findViewById<Button>(R.id.btnSaveBudget)
        val lvBudgets   = findViewById<ListView>(R.id.lvBudgets)

        // Load categories into spinner
        val categoriesJson = prefs.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)
        val categoryList = mutableListOf<String>()
        for (i in 0 until categoriesArray.length()) {
            categoryList.add(categoriesArray.getString(i))
        }
        if (categoryList.isEmpty()) categoryList.add("General")

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categoryList)
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = adapter

        // Show existing budgets
        fun refreshBudgets() {
            val budgetsJson = prefs.getString("budgets", "[]")
            val budgetsArray = JSONArray(budgetsJson)
            val displayList = mutableListOf<String>()
            for (i in 0 until budgetsArray.length()) {
                val obj = budgetsArray.getJSONObject(i)
                val cat = obj.getString("category")
                val limit = obj.getString("limit")
                val spent = getTotalSpentForCategory(cat)
                val percent = if (limit.toDouble() > 0)
                    ((spent / limit.toDouble()) * 100).toInt() else 0
                displayList.add("$cat: R$spent / R$limit  ($percent% used)")
            }
            lvBudgets.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, displayList)
        }
        refreshBudgets()

        btnSave.setOnClickListener {
            val category = spinnerCat.selectedItem.toString()
            val limit = etLimit.text.toString().trim()

            if (limit.isEmpty()) {
                Toast.makeText(this, "Enter a budget limit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save or update budget for this category
            val budgetsJson = prefs.getString("budgets", "[]")
            val budgetsArray = JSONArray(budgetsJson)
            var updated = false

            for (i in 0 until budgetsArray.length()) {
                if (budgetsArray.getJSONObject(i).getString("category") == category) {
                    budgetsArray.getJSONObject(i).put("limit", limit)
                    updated = true
                    break
                }
            }

            if (!updated) {
                budgetsArray.put(JSONObject().apply {
                    put("category", category)
                    put("limit", limit)
                })
            }

            prefs.edit().putString("budgets", budgetsArray.toString()).apply()
            Toast.makeText(this, "Budget saved!", Toast.LENGTH_SHORT).show()
            etLimit.text.clear()
            refreshBudgets()
        }
    }

    // Calculates total spent for a given category from saved expenses
    private fun getTotalSpentForCategory(category: String): Double {
        val expensesJson = prefs.getString("expenses", "[]")
        val expensesArray = JSONArray(expensesJson)
        var total = 0.0
        for (i in 0 until expensesArray.length()) {
            val obj = expensesArray.getJSONObject(i)
            if (obj.getString("category") == category) {
                total += obj.getString("amount").toDouble()
            }
        }
        return total
    }
}