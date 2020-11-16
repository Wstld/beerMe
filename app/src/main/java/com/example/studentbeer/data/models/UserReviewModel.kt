package com.example.studentbeer.data.models

data class UserReviewModel (
    val barFireBaseId:String = "",
    val userRating:Double =0.0,
    val barName:String = "",
    val userDefinedBeerPrice:Int = 0
        )