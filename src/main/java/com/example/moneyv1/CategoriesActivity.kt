package com.example.moneyv1

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

    private lateinit var categoryDao: CategoryDao
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        // Initialize the database and CategoryDao
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app-database").build()
        categoryDao = db.categoryDao()

        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the title for the categories screen
        val backgroundView = findViewById<ConstraintLayout>(R.id.backgroundInclude)
        val textHeading = backgroundView.findViewById<TextView>(R.id.textHeading)
        textHeading.text = "Categories"

        // Set up the add category button
        val imgAddCategory = findViewById<ImageView>(R.id.imgAddCategory)
        imgAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        // Load categories and display them in the RecyclerView
        loadCategories()
    }

    // Load all categories from the database and display them
    private fun loadCategories() {
        lifecycleScope.launch {
            val categories = categoryDao.getAllCategories()
            recyclerView.adapter = CategoryAdapter(categories)
        }
    }

    // Show the dialog to add a new category
    private fun showAddCategoryDialog() {
        // Inflate the dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.new_category, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val editTextCategoryName = dialogView.findViewById<EditText>(R.id.editTextCategoryName)
        val imagePreview = dialogView.findViewById<ImageView>(R.id.imagePreview)
        val btnSaveCategory = dialogView.findViewById<Button>(R.id.btnSaveCategory)
        val btnCancelCategory = dialogView.findViewById<Button>(R.id.btnCancelCategory)

        // Set up save button
        btnSaveCategory.setOnClickListener {
            val categoryName = editTextCategoryName.text.toString()
            if (categoryName.isNotEmpty()) {
                // Save the category to the database
                val category = Category(name = categoryName, picture = "default_image_url")
                lifecycleScope.launch {
                    categoryDao.insert(category)
                    Toast.makeText(this@CategoriesActivity, "Category added", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()

                    // Reload the categories after adding a new one
                    loadCategories()
                }
            } else {
                Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up cancel button
        btnCancelCategory.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
