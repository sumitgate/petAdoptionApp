package com.example.petadoptionapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PetAdoptionDB"
        private const val DATABASE_VERSION = 1

        // User Table
        private const val TABLE_USER = "User"
        private const val COL_USER_ID = "UserID"
        private const val COL_NAME = "Name"
        private const val COL_USERNAME = "Username"
        private const val COL_PASSWORD = "Password"

        // Pet Table
        private const val TABLE_PET = "Pet"
        private const val COL_PET_ID = "PetID"
        private const val COL_NAME_PET = "PetName"
        private const val COL_BREED = "Breed"
        private const val COL_AGE = "Age"
        private const val COL_AREA_CODE = "AreaCode"
        private const val COL_IMAGE = "Image" // Store image as BLOB
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT,
                $COL_USERNAME TEXT,
                $COL_PASSWORD TEXT
            )
        """
        val createPetTable = """
            CREATE TABLE $TABLE_PET (
                $COL_PET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME_PET TEXT,
                $COL_BREED TEXT,
                $COL_AGE TEXT,
                $COL_AREA_CODE TEXT,
                $COL_IMAGE BLOB
            )
        """
        db?.execSQL(createUserTable)
        db?.execSQL(createPetTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PET")
        onCreate(db)
    }

    // Add user method
    fun addUser(name: String, username: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_USERNAME, username)
            put(COL_PASSWORD, password)
        }
        return db.insert(TABLE_USER, null, values)
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USER, arrayOf(COL_USER_ID), "$COL_USERNAME=? AND $COL_PASSWORD=?", arrayOf(username, password), null, null, null)
        val exists = cursor.use { it.moveToFirst() }
        return exists
    }

    fun getUserName(username: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_USER, arrayOf(COL_NAME), "$COL_USERNAME=?", arrayOf(username), null, null, null)
        return cursor.use {
            if (it.moveToFirst()) {
                it.getString(it.getColumnIndexOrThrow(COL_NAME))
            } else {
                null
            }
        }
    }

    // Method to add a pet with a ByteArray image
    fun addPet(name: String, breed: String, age: String, areaCode: String, image: ByteArray): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME_PET, name)
            put(COL_BREED, breed)
            put(COL_AGE, age)
            put(COL_AREA_CODE, areaCode)
            put(COL_IMAGE, image)
        }
        return db.insert(TABLE_PET, null, values)
    }

    fun getAllPets(): List<Pet> {
        return getPets() // Calls getPets without filters to get all pets
    }

    // Method to retrieve pets by area code
    fun searchPetsByAreaCode(areaCode: String): List<Pet> {
        return getPets(areaCode) // Calls getPets with area code filter
    }

    // Retrieve pets from database, including image ByteArray
    fun getPets(areaCode: String? = null): List<Pet> {
        val db = this.readableDatabase
        val petList = mutableListOf<Pet>()
        val cursor = if (areaCode.isNullOrEmpty()) {
            db.query(TABLE_PET, null, null, null, null, null, null)
        } else {
            db.query(TABLE_PET, null, "$COL_AREA_CODE=?", arrayOf(areaCode), null, null, null)
        }
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val pet = Pet(
                        it.getInt(it.getColumnIndexOrThrow(COL_PET_ID)),
                        it.getString(it.getColumnIndexOrThrow(COL_NAME_PET)),
                        it.getString(it.getColumnIndexOrThrow(COL_BREED)),
                        it.getString(it.getColumnIndexOrThrow(COL_AGE)),
                        it.getString(it.getColumnIndexOrThrow(COL_AREA_CODE)),
                        it.getBlob(it.getColumnIndexOrThrow(COL_IMAGE)) // Retrieve image as ByteArray
                    )
                    petList.add(pet)
                } while (it.moveToNext())
            }
        }
        return petList
    }

    fun deletePet(petId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_PET, "$COL_PET_ID=?", arrayOf(petId.toString()))
    }

    fun getPetById(petId: Int): Pet? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_PET,
            arrayOf(COL_PET_ID, COL_NAME_PET, COL_BREED, COL_AGE, COL_AREA_CODE, COL_IMAGE),
            "$COL_PET_ID = ?",
            arrayOf(petId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Pet(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_PET_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COL_NAME_PET)),
                    breed = it.getString(it.getColumnIndexOrThrow(COL_BREED)),
                    age = it.getString(it.getColumnIndexOrThrow(COL_AGE)),
                    areaCode = it.getString(it.getColumnIndexOrThrow(COL_AREA_CODE)),
                    image = it.getBlob(it.getColumnIndexOrThrow(COL_IMAGE)) // Get ByteArray for image
                )
            }
        }
        return null
    }
}
