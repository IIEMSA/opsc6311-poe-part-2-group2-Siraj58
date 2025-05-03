package com.example.moneyv1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class TransactionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)

        val addEntryButton: ImageButton = findViewById(R.id.imgAddEntry)
        val viewEntriesButton: ImageButton = findViewById(R.id.imgViewEntries)

        val backgroundView = findViewById<ConstraintLayout>(R.id.backgroundInclude)
        val textHeading = backgroundView.findViewById<TextView>(R.id.textHeading)
        textHeading.text = "Transactions"

        // Navigate to AddExpenseActivity
        addEntryButton.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        // Placeholder for viewing entries
        viewEntriesButton.setOnClickListener {
            // Replace with  actual ViewEntriesActivity
            val intent = Intent(this, ViewEntriesActivity::class.java)
            startActivity(intent)
        }



        // Navigation button setup (from included bottom_nav_view)
        val homeBtn: ImageButton = findViewById(R.id.imgbtnHome)
        val transactionsBtn: ImageButton = findViewById(R.id.imgbtnTransactions)
        val categoriesBtn: ImageButton = findViewById(R.id.imgbtnCategories)

// Set click listeners
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