package com.example.petadoptionapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AddPetActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var etPetName: EditText
    private lateinit var etBreed: EditText
    private lateinit var etAge: EditText
    private lateinit var etAreaCode: EditText
    private lateinit var etImageUri: EditText
    private lateinit var btnSavePet: Button
    private lateinit var btnSelectImage: Button
    private lateinit var ivPreview: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pet)

        db = DatabaseHelper(this)
        etPetName = findViewById(R.id.etPetName)
        etBreed = findViewById(R.id.etBreed)
        etAge = findViewById(R.id.etAge)
        etAreaCode = findViewById(R.id.etAreaCode)
        etImageUri = findViewById(R.id.etImageUri)
        btnSavePet = findViewById(R.id.btnSavePet)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        ivPreview = findViewById(R.id.ivPreview)

        btnSelectImage.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        btnSavePet.setOnClickListener {
            val name = etPetName.text.toString()
            val breed = etBreed.text.toString()
            val age = etAge.text.toString()
            val areaCode = etAreaCode.text.toString()

            val imageUri = Uri.parse(etImageUri.text.toString())
            val imageData = contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
            if (imageData != null) {
                // Pass imageData to addPet
                val result = db.addPet(name, breed, age, areaCode, imageData)
                if (result > 0) {
                    Toast.makeText(this, "Pet added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to add pet", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            // Request permission with an explanation, if necessary
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Permission is needed to select an image from your gallery.", Toast.LENGTH_LONG).show()
            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                etImageUri.setText(it.toString())
                ivPreview.setImageURI(it)  // Display selected image
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                etImageUri.setText(imageUri.toString())  // Display the URI in the EditText field
            } else {
                Toast.makeText(this, "Unable to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                openGallery()
            } else {
                Toast.makeText(this, "Permission required to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        const val REQUEST_CODE_STORAGE_PERMISSION = 1
        const val REQUEST_CODE_GALLERY = 2
    }

}
