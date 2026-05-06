package com.example.budgettrackerapp

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

// Screen for adding a new expense entry
class AddExpenseActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        val etAmount      = findViewById<EditText>(R.id.etAmount)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val spinnerCat    = findViewById<Spinner>(R.id.spinnerCategory)
        val tvDate        = findViewById<TextView>(R.id.tvDate)
        val btnPickDate   = findViewById<Button>(R.id.btnPickDate)
        val btnSave       = findViewById<Button>(R.id.btnSave)
        val lvExpenses    = findViewById<ListView>(R.id.lvExpenses)

        // Load saved categories into spinner
        val categoriesJson = prefs.getString("categories", "[]")
        val categoriesArray = JSONArray(categoriesJson)
        val categoryList = mutableListOf<String>()
        for (i in 0 until categoriesArray.length()) {
            categoryList.add(categoriesArray.getString(i))
        }
        if (categoryList.isEmpty()) categoryList.add("General")

        val spinnerAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, categoryList)
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        spinnerCat.adapter = spinnerAdapter

        // Date picker
        var selectedDate = ""
        btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                selectedDate = "$d/${m + 1}/$y"
                tvDate.text = selectedDate
            }, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Load and display existing expenses
        fun refreshList() {
            val expensesJson = prefs.getString("expenses", "[]")
            val expensesArray = JSONArray(expensesJson)
            val displayList = mutableListOf<String>()
            for (i in 0 until expensesArray.length()) {
                val obj = expensesArray.getJSONObject(i)
                displayList.add(
                    "R${obj.getString("amount")} — " +
                            "${obj.getString("description")} " +
                            "[${obj.getString("category")}] " +
                            "${obj.getString("date")}"
                )
            }
            lvExpenses.adapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, displayList)
        }
        refreshList()

        // Save new expense
        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val category = spinnerCat.selectedItem.toString()

            if (amount.isEmpty() || description.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this,
                    "Please fill in all fields and pick a date",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save expense as JSON object in SharedPreferences
            val expensesJson = prefs.getString("expenses", "[]")
            val expensesArray = JSONArray(expensesJson)
            val newExpense = JSONObject().apply {
                put("amount", amount)
                put("description", description)
                put("category", category)
                put("date", selectedDate)
            }
            expensesArray.put(newExpense)
            prefs.edit().putString("expenses", expensesArray.toString()).apply()

            // Update total spent
            val currentTotal = prefs.getFloat("totalSpent", 0f)
            prefs.edit().putFloat("totalSpent",
                currentTotal + amount.toFloat()).apply()

            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            etAmount.text.clear()
            etDescription.text.clear()
            tvDate.text = "No date selected"
            selectedDate = ""
            refreshList()
        }
    }
}