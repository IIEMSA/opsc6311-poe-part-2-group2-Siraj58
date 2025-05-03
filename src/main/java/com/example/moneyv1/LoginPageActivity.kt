package com.example.moneyv1

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)
		
		findViewById<EditText>(R.id.editTextText).text.clear()
		
        val emailField = findViewById<EditText>(R.id.editTextText)
        val passwordField = findViewById<EditText>(R.id.editTextTextPassword2)
        val loginButton = findViewById<Button>(R.id.button)
        val signupButton = findViewById<Button>(R.id.button2)

        emailField.setOnClickListener{
            findViewById<EditText>(R.id.editTextText).text.clear()
        }

        val sharedPref = getSharedPreferences("userData", MODE_PRIVATE)

        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()

            val storedEmail = sharedPref.getString("email", "")
            val storedPassword = sharedPref.getString("password", "")

            if (!email.equals("example@gmail.com"))
            {
                if (email == storedEmail && password == storedPassword) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java)) // or HomePageActivity
                    finish()
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
             else {
                Toast.makeText(this, "Input an email and password", Toast.LENGTH_SHORT).show()
            }
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignUpPageActivity::class.java))
        }
    }
}
