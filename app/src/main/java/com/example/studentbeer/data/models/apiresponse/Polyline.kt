package com.example.studentbeer.data.models.apiresponse


import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName("points")
    val points: String
)