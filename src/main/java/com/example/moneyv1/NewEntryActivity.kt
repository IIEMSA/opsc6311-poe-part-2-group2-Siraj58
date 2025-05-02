package com.example.moneyv1

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneyv1.data.AppDatabase
import com.example.moneyv1.model.Category
import com.example.moneyv1.model.TransactionEntry
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

class NewEntryActivity : AppCompatActivity() {

    private var selectedCategoryId: Int? = null
    private var selectedImageUri: Uri? = null
    private lateinit var receiptImageView: ImageView
    private lateinit var db: AppDatabase
    private lateinit var categoryList: List<Category>
    private lateinit var saveButton: Button
    private lateinit var categorySpinner: Spinner
    private lateinit var amountInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var startTimeInput: EditText
    private lateinit var descriptionInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_entry)

        db = AppDatabase.getDatabase(this)
        val backButton: ImageButton = findViewById(R.id.imgbackbtn)
        backButton.setOnClickListener { finish() }

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner)
        receiptImageView = findViewById(R.id.receiptImageView)
        saveButton = findViewById(R.id.saveButton)
        amountInput = findViewById(R.id.amountInput)
        dateInput = findViewById(R.id.dateInput)
        startTimeInput = findViewById(R.id.startTimeInput)
        descriptionInput = findViewById(R.id.descriptionInput)

        // Initially disable save button
        saveButton.isEnabled = false
        saveButton.alpha = 0.5f

        // Load categories from DB
        lifecycleScope.launch {
            categoryList = db.appDao().getAllCategories()
            val names = categoryList.map { it.name }
            val adapter = ArrayAdapter(this@NewEntryActivity, android.R.layout.simple_spinner_dropdown_item, names)
            categorySpinner.adapter = adapter
        }

//        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                selectedCategoryId = categoryList.getOrNull(position)?.id
//                validateForm()
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                selectedCategoryId = null
//                validateForm()
//            }
//        }

        // Add text change listeners for validation
        amountInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { validateForm() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

//        dateInput.setOnClickListener { showDatePicker(dateInput) }
//        startTimeInput.setOnClickListener { showTimePicker(startTimeInput) }
//        findViewById<EditText>(R.id.endTimeInput).setOnClickListener { showTimePicker(it) }

        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                receiptImageView.setImageURI(it)
            }
        }
        selectImageButton.setOnClickListener { imagePickerLauncher.launch("image/*") }

        saveButton.setOnClickListener {
            if (validateForm()) {
                saveEntry()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate category
        if (selectedCategoryId == null) {
            categorySpinner.background = ContextCompat.getDrawable(this, R.drawable.error_background)
            isValid = false
        } else {
            categorySpinner.background = ContextCompat.getDrawable(this, R.color.light_white)
        }

        // Validate amount
        val amountText = amountInput.text.toString()
        if (amountText.isBlank()) {
            amountInput.error = "Amount is required"
            isValid = false
        } else if (amountText.toDoubleOrNull() == null) {
            amountInput.error = "Enter a valid amount"
            isValid = false
        } else {
            amountInput.error = null
        }

        // Validate date
        if (dateInput.text.isBlank()) {
            dateInput.error = "Date is required"
            isValid = false
        } else {
            dateInput.error = null
        }

        // Validate start time
        if (startTimeInput.text.isBlank()) {
            startTimeInput.error = "Start time is required"
            isValid = false
        } else {
            startTimeInput.error = null
        }

        // Validate description
        if (descriptionInput.text.isBlank()) {
            descriptionInput.error = "Description is required"
            isValid = false
        } else {
            descriptionInput.error = null
        }

        // Update save button state
        saveButton.isEnabled = isValid
        saveButton.alpha = if (isValid) 1.0f else 0.5f

        return isValid
    }

    private fun saveEntry() {
        val categoryId = selectedCategoryId!!
        val date = dateInput.text.toString()
        val startTime = startTimeInput.text.toString()
        val endTime = findViewById<EditText>(R.id.endTimeInput).text.toString()
        val description = descriptionInput.text.toString()
        val amount = amountInput.text.toString().toDouble()
        val transactionTypeGroup = findViewById<RadioGroup>(R.id.rgTransactionType)
        val isExpense = when (transactionTypeGroup.checkedRadioButtonId) {
            R.id.rbExpense -> true
            R.id.rbIncome -> false
            else -> true
        }

        val timestamp = parseDateTimeToMillis(date, startTime)
        val imageUriString = selectedImageUri?.toString()

        lifecycleScope.launch {
            db.appDao().insertTransaction(
                TransactionEntry(
                    categoryId = categoryId,
                    title = description,
                    cost = amount,
                    isExpense = isExpense,
                    timestamp = timestamp,
                    imageUri = imageUriString
                )
            )
            runOnUiThread {
                Toast.makeText(this@NewEntryActivity, "Entry saved successfully!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun showDatePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                target.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                target.setText(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun parseDateTimeToMillis(date: String, time: String): Long {
        return try {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dt = sdf.parse("$date $time")
            dt?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val fileName = "receipt_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
