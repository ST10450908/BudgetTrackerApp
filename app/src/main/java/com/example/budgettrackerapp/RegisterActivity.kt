package com.example.budgettrackerapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// Allows new users to create an account saved in SharedPreferences
class RegisterActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE)

        val etUsername        = findViewById<EditText>(R.id.etUsername)
        val etPassword        = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister       = findViewById<Button>(R.id.btnRegister)
        val tvLogin           = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm  = etConfirmPassword.text.toString().trim()

            when {
                username.isEmpty() || password.isEmpty() ->
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                username.length < 3 ->
                    Toast.makeText(this, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show()
                password.length < 6 ->
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                password != confirm ->
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                else -> {
                    // Save credentials to SharedPreferences
                    prefs.edit()
                        .putString("username", username)
                        .putString("password", password)
                        .putBoolean("isLoggedIn", true)
                        .apply()
                    Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
            }
        }

        tvLogin.setOnClickListener { finish() }
    }
}