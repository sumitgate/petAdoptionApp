package com.example.petadoptionapp

data class Pet(
    val id: Int,
    val name: String,
    val breed: String,
    val age: String,
    val areaCode: String,
    val image: ByteArray // Store image as a ByteArray
) {
    companion object {
        const val TABLE_NAME = "pets"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_BREED = "breed"
        const val COLUMN_AGE = "age"
        const val COLUMN_AREA_CODE = "area_code"
        const val COLUMN_IMAGE = "image"
    }
}
