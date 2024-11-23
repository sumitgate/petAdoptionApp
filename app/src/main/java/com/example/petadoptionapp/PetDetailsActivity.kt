package com.example.petadoptionapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PetDetailsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var tvPetDetails: TextView
    private lateinit var ivPetImage: ImageView
    private lateinit var btnAdoptPet: Button
    private lateinit var btnGoBack: Button
    private var petId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

        db = DatabaseHelper(this)
        tvPetDetails = findViewById(R.id.tvPetDetails)
        ivPetImage = findViewById(R.id.ivPetImage)
        btnAdoptPet = findViewById(R.id.btnAdoptPet)
        btnGoBack = findViewById(R.id.btnGoBack)

        petId = intent.getIntExtra("petId", -1)
        val pet = db.getPetById(petId)

        pet?.let {
            // Set pet details
            tvPetDetails.text = """
                Name: ${it.name}
                Breed: ${it.breed}
                Age: ${it.age}
                Area Code: ${it.areaCode}
            """.trimIndent()

            // Decode and display pet image
            val imageByteArray = it.image
            if (imageByteArray.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                ivPetImage.setImageBitmap(bitmap)
            } else {
                ivPetImage.setImageResource(R.drawable.ic_launcher_foreground) // Default image
            }
        }

        btnAdoptPet.setOnClickListener {
            if (db.deletePet(petId) > 0) {
                Toast.makeText(this, "Pet adopted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to adopt pet", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoBack.setOnClickListener {
            finish()
        }
    }
}
