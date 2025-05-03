package com.example.moneyv1

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var dateInput: EditText
    private lateinit var startTimeInput: EditText
    private lateinit var endTimeInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var minGoalInput: EditText
    private lateinit var maxGoalInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var titleInput: EditText

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val CAMERA_CAPTURE_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize views
        val rgTransactionType: RadioGroup = findViewById(R.id.rgTransactionType)
        val rbIncome: RadioButton = findViewById(R.id.rbIncome)
        val rbExpense: RadioButton = findViewById(R.id.rbExpense)
        val backButton: Button = findViewById(R.id.backButton)
        val categoryNameText: TextView = findViewById(R.id.categoryTitle)
        val categoryIcon: ImageView = findViewById(R.id.categoryIcon)
        val noteEditText: EditText = findViewById(R.id.noteInput)
        val selectImageButton: Button = findViewById(R.id.selectImageButton)
        val takePictureButton: Button = findViewById(R.id.TakePicture)
        val saveExpenseButton: Button = findViewById(R.id.saveButton)

        imageView = findViewById(R.id.receiptImageView)
        dateInput = findViewById(R.id.dateInput)
        startTimeInput = findViewById(R.id.startTimeInput)
        endTimeInput = findViewById(R.id.endTimeInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        minGoalInput = findViewById(R.id.minGoalInput)
        maxGoalInput = findViewById(R.id.maxGoalInput)
        amountInput = findViewById(R.id.amountInput)
        titleInput = findViewById(R.id.TitleInput)

        // Handle image result from camera
        val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val byteArray = data?.getByteArrayExtra("captured_image_bytes")
                byteArray?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    imageView.setImageBitmap(bitmap)

                    // Optionally save bitmap to file to get URI for RoomDB
                    val uri = saveBitmapToCache(bitmap)
                    selectedImageUri = uri
                }

            }
        }

        // Back button functionality
        backButton.setOnClickListener {
            finish()
        }

        // Category info from Intent
        val category = intent.getStringExtra("category")
        val iconResId = intent.getIntExtra("category_icon", R.drawable.ic_placeholder)
        categoryNameText.text = category ?: "Unknown"
        categoryIcon.setImageResource(iconResId)

        // Category spinner setup
        val categories = arrayOf("Food", "Transport", "Entertainment", "Shopping")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Date and time pickers
        dateInput.setOnClickListener { showDatePicker() }
        startTimeInput.setOnClickListener { showTimePicker(true) }
        endTimeInput.setOnClickListener { showTimePicker(false) }

        // Image from gallery
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // Capture photo from camera
        takePictureButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraResultLauncher.launch(intent)
        }

        // Save expense logic
        saveExpenseButton.setOnClickListener {
            val amount = amountInput.text.toString().toFloatOrNull()
            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val minGoal = minGoalInput.text.toString().toIntOrNull() ?: 0
            val maxGoal = maxGoalInput.text.toString().toIntOrNull() ?: 0
            val selectedCategory = categorySpinner.selectedItem.toString()
            val date = dateInput.text.toString()
            val startTime = startTimeInput.text.toString()
            val endTime = endTimeInput.text.toString()
            val isIncome = rgTransactionType.checkedRadioButtonId == rbIncome.id

            val entry = Entry(
                expense = !isIncome,
                amount = amount,
                date = date,
                title = title,
                startTime = startTime,
                endTime = endTime,
                description = description,
                category = selectedCategory,
                minGoal = minGoal,
                maxGoal = maxGoal,
                imagepath = selectedImageUri?.path ?: "",
                imageUri = selectedImageUri?.toString() ?: ""
            )

            // Save to Room database
            val db = AppDatabase.getDatabase(this)
            lifecycleScope.launch(Dispatchers.IO) {
                db.entryDao().insert(entry)
            }

            Toast.makeText(this, "Saved to RoomDB", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(this,
            { _, year, month, day ->
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                dateInput.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val filename = "captured_image_${System.currentTimeMillis()}.png"
        val file = File(cacheDir, filename)
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        return Uri.fromFile(file)
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(this,
            { _, hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                if (isStartTime) {
                    startTimeInput.setText(formatted)
                } else {
                    endTimeInput.setText(formatted)
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    selectedImageUri = data?.data
                    imageView.setImageURI(selectedImageUri)
                }
                CAMERA_CAPTURE_CODE -> {
                    val imageUriString = data?.getStringExtra("captured_image_uri")
                    if (!imageUriString.isNullOrEmpty()) {
                        selectedImageUri = Uri.parse(imageUriString)
                        imageView.setImageURI(selectedImageUri)
                    }
                }
            }
        }
    }
}
