package com.example.moneyv1

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import android.app.DatePickerDialog


class ViewEntriesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EntryAdapter
    private lateinit var entryDao: EntryDao
    private lateinit var spinnerCategories: Spinner
    private lateinit var editStartDate: EditText
    private lateinit var editEndDate: EditText
    private lateinit var rgTransactionType: RadioGroup
    private lateinit var btnSearch: Button
    private lateinit var btnClearFilters: Button
    private lateinit var tvSummaryResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.view_entries)

        val backButton: Button = findViewById(R.id.backButton)
        recyclerView = findViewById(R.id.recyclerFilteredEntries)
        spinnerCategories = findViewById(R.id.spinnerCategories)
        editStartDate = findViewById(R.id.editStartDate)
        editEndDate = findViewById(R.id.editEndDate)
        rgTransactionType = findViewById(R.id.rgTransactionType)
        btnSearch = findViewById(R.id.btnSearch)
        btnClearFilters = findViewById(R.id.btnClearFilters)
        tvSummaryResult = findViewById(R.id.tvSummaryResult)

        editStartDate.setOnClickListener { showDatePicker(editStartDate) }
        editEndDate.setOnClickListener { showDatePicker(editEndDate) }

        val db = AppDatabase.getDatabase(applicationContext)
        entryDao = db.entryDao()
        recyclerView.layoutManager = LinearLayoutManager(this)

        val categories = listOf("All", "Food", "Transport", "Entertainment", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategories.adapter = spinnerAdapter

        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Search Filter Button
        btnSearch.setOnClickListener {
            val selectedCategory = spinnerCategories.selectedItem.toString()
            val startDate = editStartDate.text.toString()
            val endDate = editEndDate.text.toString()
            val isIncome: Boolean? = when (rgTransactionType.checkedRadioButtonId) {
                R.id.rbIncome -> true
                R.id.rbExpense -> false
                else -> null
            }

            lifecycleScope.launch {
                val filteredEntries = filterEntries(selectedCategory, startDate, endDate, isIncome)
                adapter = EntryAdapter(filteredEntries)
                recyclerView.adapter = adapter

                val total = filteredEntries.sumOf { if (it.expense) -it.amount.toDouble() else it.amount.toDouble() }
                tvSummaryResult.text = "Total: R${"%.2f".format(total)}"
            }
        }

        // Clear Filters Button
        btnClearFilters.setOnClickListener {
            spinnerCategories.setSelection(0)
            editStartDate.text.clear()
            editEndDate.text.clear()
            rgTransactionType.clearCheck()

            lifecycleScope.launch {
                val allEntries = entryDao.getAllEntries()
                adapter = EntryAdapter(allEntries)
                recyclerView.adapter = adapter

                val total = allEntries.sumOf { if (it.expense) -it.amount.toDouble() else it.amount.toDouble() }
                tvSummaryResult.text = "Total: R${"%.2f".format(total)}"
            }
        }
    }

    private suspend fun filterEntries(category: String, startDate: String, endDate: String, isIncome: Boolean?): List<Entry> {
        val entries = entryDao.getAllEntries()

        return entries.filter { entry ->
            val matchesCategory = category == "All" || entry.category == category

            val matchesStart = startDate.isEmpty() || entry.date >= startDate
            val matchesEnd = endDate.isEmpty() || entry.date <= endDate
            val matchesDate = matchesStart && matchesEnd

            val matchesTransactionType = when (isIncome) {
                true -> !entry.expense
                false -> entry.expense
                null -> true
            }

            matchesCategory && matchesDate && matchesTransactionType
        }
    }

    private fun showDatePicker(targetEditText: EditText) {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
            targetEditText.setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }
}
