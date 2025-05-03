package com.example.moneyv1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.moneyv1.databinding.ActivityCameraBinding
import java.io.ByteArrayOutputStream


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private val picId = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate layout using view binding
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set back button to finish the activity
        binding.backButton.setOnClickListener {
            finish()
        }

        // Handle photo FAB click
        binding.photoFab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }
    }

    // Open the camera
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, picId)
    }

    // Request CAMERA permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            picId
        )
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == picId && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            // Permission denied - Inform the user or set a fallback image
            binding.imgSavedPhoto.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }



    // Handle the captured image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == picId && resultCode == Activity.RESULT_OK) {
            val photo = data?.extras?.get("data") as? Bitmap
            photo?.let {
                // Save bitmap to cache and get URI
                val filename = "captured_image_${System.currentTimeMillis()}.jpg"
                val stream = openFileOutput(filename, MODE_PRIVATE)
                it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.close()
                val imageFile = getFileStreamPath(filename)
                val imageUri = Uri.fromFile(imageFile)

                // Set result to send back to AddExpenseActivity
                val resultIntent = Intent().apply {
                    putExtra("captured_image_uri", imageUri.toString())
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

}
