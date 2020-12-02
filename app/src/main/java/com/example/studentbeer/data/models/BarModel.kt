package com.example.studentbeer.data.models

data class BarModel(
    val barName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val streetName: String = "",
    val openHours: String = "",
    val rating: Double = 3.0,
    val beerPrice: Int = 0,
    var id: String = ""
)