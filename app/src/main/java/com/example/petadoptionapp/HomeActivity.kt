package com.example.petadoptionapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnAddPet = findViewById<Button>(R.id.btnAddPet)
        val btnDisplayPets = findViewById<Button>(R.id.btnDisplayPets)
        val btnLogout = findViewById<Button>(R.id.btnLogout)  // Logout button

        // Get name from intent and display
        val name = intent.getStringExtra("name")
        tvWelcome.text = "Welcome, $name!"

        btnAddPet.setOnClickListener {
            startActivity(Intent(this, AddPetActivity::class.java))
        }

        btnDisplayPets.setOnClickListener {
            startActivity(Intent(this, DisplayPetsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            // Clear any stored user session data if applicable
            // Redirect to the main activity or login screen
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()  // Finish current activity to prevent going back to it
        }
    }
}
