package com.example.moneyv1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class TransactionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        val addEntryButton: ImageButton = findViewById(R.id.imgAddEntry)
        val viewEntriesButton: ImageButton = findViewById(R.id.imgViewEntries)

        // Navigate to AddExpenseActivity
        addEntryButton.setOnClickListener {
            val intent = Intent(this, NewEntryActivity::class.java)
            intent.putExtra("category", "General") // or pass chosen category
            intent.putExtra("category_icon", R.drawable.bank_icon)
            startActivity(intent)
        }

        // Placeholder for viewing entries
//        viewEntriesButton.setOnClickListener {
//            // Replace with your actual ViewEntriesActivity
//            val intent = Intent(this, ViewEntriesActivity::class.java)
//            startActivity(intent)
//        }



        // Navigation button setup (from included bottom_nav_view)
        val homeBtn: ImageButton = findViewById(R.id.imgbtnHome)
        val analysisBtn: ImageButton = findViewById(R.id.imgbtnAnalysis)
        val transactionsBtn: ImageButton = findViewById(R.id.imgbtnTransactions)
        val categoriesBtn: ImageButton = findViewById(R.id.imgbtnCategories)

// Set click listeners
        homeBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

//        analysisBtn.setOnClickListener {
//            startActivity(Intent(this, AnalysisActivity::class.java))
//        }

        transactionsBtn.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        categoriesBtn.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }


    }
}
