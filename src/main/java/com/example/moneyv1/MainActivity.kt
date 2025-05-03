package com.example.moneyv1

import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private lateinit var textMinMaxGoalsValue: TextView
    private lateinit var resetGoalsButton: Button
    private lateinit var homeBtn: ImageButton
    private lateinit var analysisBtn: ImageButton
    private lateinit var transactionsBtn: ImageButton
    private lateinit var categoriesBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        textMinMaxGoalsValue = findViewById(R.id.textMinMaxGoalsValue)
        resetGoalsButton = findViewById(R.id.button4)
        homeBtn = findViewById(R.id.imgbtnHome)
        transactionsBtn = findViewById(R.id.imgbtnTransactions)
        categoriesBtn = findViewById(R.id.imgbtnCategories)

        // Setup SharedPreferences
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Load saved values or set defaults
        val savedMin = sharedPrefs.getString("min_goal", "100")
        val savedMax = sharedPrefs.getString("max_goal", "10000")
        textMinMaxGoalsValue.text = "$savedMin - $savedMax"

        // Reset goals button
        resetGoalsButton.setOnClickListener {
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_reset_goals, null)
            val editMin = view.findViewById<EditText>(R.id.editTextMin)
            val editMax = view.findViewById<EditText>(R.id.editTextMax)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Reset Monthly Goals")
                .setView(view)
                .setPositiveButton("Save") { _, _ ->
                    val minGoal = editMin.text.toString()
                    val maxGoal = editMax.text.toString()
                    if (minGoal.isNotEmpty() && maxGoal.isNotEmpty()) {
                        // Save to SharedPreferences
                        with(sharedPrefs.edit()) {
                            putString("min_goal", minGoal)
                            putString("max_goal", maxGoal)
                            apply()
                        }
                        textMinMaxGoalsValue.text = "$minGoal - $maxGoal"
                        Toast.makeText(this, "Goals updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Please enter both values", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }

        // Button listeners for navigation
        homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        transactionsBtn.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        categoriesBtn.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }
    }
}
