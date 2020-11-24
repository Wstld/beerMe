package com.example.studentbeer.data.models.apiResponse


import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @SerializedName("points")
    val points: String
)