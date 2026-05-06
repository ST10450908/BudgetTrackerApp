package com.example.budgettrackerapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

// Screen to add and manage spending categories
class CategoryActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private val categoryList = mutableListOf<String>()
    private lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        val etCategory = findViewById<EditText>(R.id.etCategory)
        val btnAdd     = findViewById<Button>(R.id.btnAddCategory)
        val lvCats     = findViewById<ListView>(R.id.lvCategories)

        // Load saved categories
        val saved = prefs.getString("categories", "[]")
        val arr = JSONArray(saved)
        for (i in 0 until arr.length()) categoryList.add(arr.getString(i))

        listAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, categoryList)
        lvCats.adapter = listAdapter

        // Add new category
        btnAdd.setOnClickListener {
            val name = etCategory.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (categoryList.contains(name)) {
                Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            categoryList.add(name)
            saveCategories()
            listAdapter.notifyDataSetChanged()
            etCategory.text.clear()
            Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show()
        }

        // Long press to delete a category
        lvCats.setOnItemLongClickListener { _, _, position, _ ->
            categoryList.removeAt(position)
            saveCategories()
            listAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
            true
        }
    }

    // Saves the current category list to SharedPreferences as JSON
    private fun saveCategories() {
        val arr = JSONArray()
        categoryList.forEach { arr.put(it) }
        prefs.edit().putString("categories", arr.toString()).apply()
    }
}