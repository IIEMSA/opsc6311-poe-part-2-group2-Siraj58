package com.example.moneyv1

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyv1.data.AppDatabase
import com.example.moneyv1.model.Category
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

//    private lateinit var adapter: CategoryAdapter
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var overlay: View
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var nameField: EditText
    private val categories = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewCategories)
        overlay = findViewById(R.id.newCategoryOverlay)
        saveBtn = overlay.findViewById(R.id.btnSaveCategory)
        cancelBtn = overlay.findViewById(R.id.btnCancelCategory)
        nameField = overlay.findViewById(R.id.editTextCategoryName)

//        adapter = CategoryAdapter(categories) {
//            if (it.name == "Add") overlay.visibility = View.VISIBLE
//            // else: handle category click (e.g., open entries for this category)
//        }
        recyclerView.layoutManager = GridLayoutManager(this, 3)
//        recyclerView.adapter = adapter

        loadCategoriesFromDb()

        saveBtn.setOnClickListener {
            val name = nameField.text.toString()
            if (name.isNotEmpty()) {
                lifecycleScope.launch {
//                    db.appDao().insertCategory(Category(name = name))
                    loadCategoriesFromDb()
                }
                overlay.visibility = View.GONE
                nameField.text.clear()
                Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener {
            overlay.visibility = View.GONE
            nameField.text.clear()
        }
    }

    private fun loadCategoriesFromDb() {
        lifecycleScope.launch {
            val dbCategories = db.appDao().getAllCategories()
            categories.clear()
            categories.addAll(dbCategories)
            // Add the special 'Add' block at the end
//            categories.add(Category(name = "Add"))
//            adapter.notifyDataSetChanged()
        }
    }
}
