package com.example.studentbeer.data

import com.example.studentbeer.data.models.apiResponse.DirectionsModel
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {

    @GET("directions/json?")
  suspend fun getDirections(
        @Query("origin")startLatLng: String,
        @Query("destination")endLatLng: String,
        @Query("mode")travleMode: String = "walking",
        @Query("key")apiKey:String = "AIzaSyBFA8D_UGwVkXwDweuRxYiTmmal_kfPcwM"
    ):DirectionsModel

    }

