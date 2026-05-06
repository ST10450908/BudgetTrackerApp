package com.example.budgettrackerapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

// Shows a text-based spending summary per category
class ReportsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        val tvReport = findViewById<TextView>(R.id.tvReport)

        val expensesJson = prefs.getString("expenses", "[]")
        val expensesArray = JSONArray(expensesJson)

        // Calculate total per category
        val categoryTotals = mutableMapOf<String, Double>()
        var grandTotal = 0.0

        for (i in 0 until expensesArray.length()) {
            val obj = expensesArray.getJSONObject(i)
            val cat = obj.getString("category")
            val amount = obj.getString("amount").toDouble()
            categoryTotals[cat] = (categoryTotals[cat] ?: 0.0) + amount
            grandTotal += amount
        }

        if (categoryTotals.isEmpty()) {
            tvReport.text = "No expenses recorded yet."
            return
        }

        // Build the report text
        val sb = StringBuilder()
        sb.appendLine("📊 Spending Report")
        sb.appendLine("─────────────────────")
        sb.appendLine()

        categoryTotals.entries.sortedByDescending { it.value }.forEach { (cat, total) ->
            val percent = if (grandTotal > 0)
                ((total / grandTotal) * 100).toInt() else 0
            val bar = "█".repeat(percent / 5) // Simple bar chart using characters
            sb.appendLine("$cat")
            sb.appendLine("  R${"%.2f".format(total)} ($percent%)")
            sb.appendLine("  $bar")
            sb.appendLine()
        }

        sb.appendLine("─────────────────────")
        sb.appendLine("Total Spent: R${"%.2f".format(grandTotal)}")

        tvReport.text = sb.toString()
    }
}