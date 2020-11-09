package com.example.studentbeer.data.models

data class BarModel(
    val barName: String, val latitude: Double,
    val longitude: Double, val streetName: String,
    val openHours: String, val rating: Double,
    val beerPrice: Int, var id: String = ""
)