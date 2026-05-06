package com.example.budgettrackerapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// MainActivity is the Login screen — first screen the user sees
class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        // If user already logged in, skip to dashboard
        if (prefs.getBoolean("isLoggedIn", false)) {
            goToDashboard()
            return
        }

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin   = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check saved credentials from SharedPreferences
            val savedUser = prefs.getString("username", "")
            val savedPass = prefs.getString("password", "")

            if (username == savedUser && password == savedPass) {
                prefs.edit().putBoolean("isLoggedIn", true).apply()
                goToDashboard()
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish() // Remove login screen from back stack
    }
}