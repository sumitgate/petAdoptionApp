package com.example.petadoptionapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DisplayPetsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etSearchAreaCode: EditText
    private lateinit var btnGoBack: Button
    private lateinit var listViewPets: ListView
    private lateinit var petAdapter: PetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_pets)

        db = DatabaseHelper(this)
        etSearchAreaCode = findViewById(R.id.etSearchAreaCode)
        btnGoBack = findViewById(R.id.btnGoBack)
        listViewPets = findViewById(R.id.listViewPets)

        loadPetData()

        // Go Back button functionality
        btnGoBack.setOnClickListener {
            finish()
        }

        // Search functionality
        etSearchAreaCode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val areaCode = s.toString()
                if (areaCode.isNotEmpty()) {
                    petAdapter.updateData(db.searchPetsByAreaCode(areaCode))
                } else {
                    loadPetData()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Handle list item clicks to show pet details
        listViewPets.setOnItemClickListener { _, _, position, _ ->
            val pet = petAdapter.getItem(position)
            val intent = Intent(this, PetDetailsActivity::class.java)
            intent.putExtra("petId", pet?.id)
            startActivity(intent)
        }
    }

    private fun loadPetData() {
        val pets = db.getAllPets()
        petAdapter = PetAdapter(this, pets)
        listViewPets.adapter = petAdapter
    }
}
