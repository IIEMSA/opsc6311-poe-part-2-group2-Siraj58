package com.example.moneyv1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signuppage)

        val fullName = findViewById<EditText>(R.id.editTextText4)
        val email = findViewById<EditText>(R.id.editTextText2)
        val password = findViewById<EditText>(R.id.editTextTextPassword3)
        val confirmPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val signUpButton = findViewById<Button>(R.id.button3)
        val loginLink = findViewById<TextView>(R.id.textView10)

        val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)

        fullName.setOnClickListener{
            findViewById<EditText>(R.id.editTextText4).text.clear()
        }

        email.setOnClickListener{
            findViewById<EditText>(R.id.editTextText2).text.clear()
        }

        signUpButton.setOnClickListener {
            val name = fullName.text.toString().trim()
            val userEmail = email.text.toString().trim()
            val pass = password.text.toString()
            val confirmPass = confirmPassword.text.toString()

            if (name.isEmpty() || name.equals("Full name") || userEmail.isEmpty() ||
                userEmail.equals("example@gmail.com")|| pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val editor = sharedPref.edit()
                editor.putString("email", userEmail)
                editor.putString("password", pass)
                editor.apply()

                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginPageActivity::class.java))
                finish()
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginPageActivity::class.java))
        }
    }
}
