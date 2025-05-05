package com.example.moneyv1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.moneyv1.databinding.ActivityCameraBinding
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var photoFile: File
    private var capturedImageUri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.photoFab.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.backButton.setOnClickListener {
            val resultIntent = Intent()
            capturedImageUri?.let {
                resultIntent.putExtra("captured_image_uri", it.toString())
                setResult(Activity.RESULT_OK, resultIntent)
            } ?: setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Create temp file
        photoFile = File(getExternalFilesDir(null), "Receipt_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider", // must match authority in your manifest
            photoFile
        )

        capturedImageUri = uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } else {
            Log.e("CameraActivity", "No camera app found")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d("CameraActivity", "Photo captured: $capturedImageUri")

            val resultIntent = Intent().apply {
                putExtra("captured_image_uri", capturedImageUri.toString())
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}
