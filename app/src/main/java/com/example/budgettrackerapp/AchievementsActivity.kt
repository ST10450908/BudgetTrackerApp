package com.example.budgettrackerapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

// Shows gamification badges the user has earned
class AchievementsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        val tvAchievements = findViewById<TextView>(R.id.tvAchievements)

        val expensesJson = prefs.getString("expenses", "[]")
        val expensesArray = JSONArray(expensesJson)
        val expenseCount = expensesArray.length()

        val categoriesJson = prefs.getString("categories", "[]")
        val categoryCount = JSONArray(categoriesJson).length()

        val budgetsJson = prefs.getString("budgets", "[]")
        val budgetCount = JSONArray(budgetsJson).length()

        val sb = StringBuilder()
        sb.appendLine("🏆 Your Achievements")
        sb.appendLine("─────────────────────")
        sb.appendLine()

        // Check and display each achievement
        val firstSave = expenseCount >= 1
        sb.appendLine("${if (firstSave) "✅" else "🔒"} First Save")
        sb.appendLine("   Log your first expense")
        sb.appendLine()

        val budgetSetter = budgetCount >= 1
        sb.appendLine("${if (budgetSetter) "✅" else "🔒"} Budget Setter")
        sb.appendLine("   Set your first monthly budget")
        sb.appendLine()

        val categoryCreator = categoryCount > 1
        sb.appendLine("${if (categoryCreator) "✅" else "🔒"} Category Creator")
        sb.appendLine("   Create a custom category")
        sb.appendLine()

        val fiveExpenses = expenseCount >= 5
        sb.appendLine("${if (fiveExpenses) "✅" else "🔒"} ⭐ Frugal Five")
        sb.appendLine("   Log 5 expenses")
        sb.appendLine()

        val tenExpenses = expenseCount >= 10
        sb.appendLine("${if (tenExpenses) "✅" else "🔒"} 💰 Money Tracker")
        sb.appendLine("   Log 10 expenses")
        sb.appendLine()

        val twentyExpenses = expenseCount >= 20
        sb.appendLine("${if (twentyExpenses) "✅" else "🔒"} 🏅 Budget Pro")
        sb.appendLine("   Log 20 expenses")
        sb.appendLine()

        // Count unlocked
        val unlocked = listOf(firstSave, budgetSetter, categoryCreator,
            fiveExpenses, tenExpenses, twentyExpenses).count { it }
        sb.appendLine("─────────────────────")
        sb.appendLine("$unlocked / 6 badges earned")

        tvAchievements.text = sb.toString()
    }
}