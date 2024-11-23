package com.example.petadoptionapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ArrayAdapter

class PetAdapter(context: Context, private val pets: List<Pet>) :
    ArrayAdapter<Pet>(context, 0, pets) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pet, parent, false)

        val pet = pets[position]
        val tvPetName = view.findViewById<TextView>(R.id.tvPetName)
        val tvBreed = view.findViewById<TextView>(R.id.tvBreed)
        val tvAge = view.findViewById<TextView>(R.id.tvAge)
        val tvAreaCode = view.findViewById<TextView>(R.id.tvAreaCode)
        val ivPetImage = view.findViewById<ImageView>(R.id.ivPetImage)

        tvPetName.text = pet.name
        tvBreed.text = "Breed: ${pet.breed}"
        tvAge.text = "Age: ${pet.age}"
        tvAreaCode.text = "Area Code: ${pet.areaCode}"

        // Decode the image from ByteArray and set it to the ImageView
        val imageByteArray = pet.image
        if (imageByteArray.isNotEmpty()) {
            val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
            ivPetImage.setImageBitmap(bitmap)
        } else {
            ivPetImage.setImageResource(R.drawable.ic_launcher_foreground) // Set a default image if none is provided
        }

        return view
    }

    fun updateData(newPets: List<Pet>) {
        clear()
        addAll(newPets)
        notifyDataSetChanged()
    }
}
