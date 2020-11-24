package com.example.studentbeer.data.models.apiResponse


import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName("points")
    val points: String
)