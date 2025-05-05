package com.example.moneyv1

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.moneyv1.CategoriesActivity.Companion.IMAGE_PICK_CODE
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

    private lateinit var categoryDao: CategoryDao
    private lateinit var recyclerView: RecyclerView

    private var selectedImageUri: Uri? = null  // Store the selected image URI

    companion object {
        private const val IMAGE_PICK_CODE = 2000
    }

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
        val btnaddImage = dialogView.findViewById<Button>(R.id.btnSelectImage)
        val btnSaveCategory = dialogView.findViewById<Button>(R.id.btnSaveCategory)
        val btnCancelCategory = dialogView.findViewById<Button>(R.id.btnCancelCategory)

        // Image from gallery
        btnaddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Set up save button
        btnSaveCategory.setOnClickListener {
            val categoryName = editTextCategoryName.text.toString()

            if (categoryName.isNotEmpty()) {
                // Save the category to the database
                val imagePath = selectedImageUri?.toString() ?: "default_image_url"
                val category = Category(name = categoryName, picture = imagePath)

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

    // Handle the image selection from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data

            // Show the selected image as a preview
            val imagePreview = (currentFocus?.rootView as? ConstraintLayout)?.findViewById<ImageView>(R.id.imagePreview)
            imagePreview?.setImageURI(selectedImageUri)
        }
    }
}
