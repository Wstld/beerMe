package com.example.studentbeer.data.models.apiResponse


import com.google.gson.annotations.SerializedName

data class Duration(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)