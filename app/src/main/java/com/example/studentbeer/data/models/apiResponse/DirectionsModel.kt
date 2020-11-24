package com.example.studentbeer.data.models.apiResponse


import com.google.gson.annotations.SerializedName

data class DirectionsModel(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint>,
    @SerializedName("routes")
    val routes: List<Route>,
    @SerializedName("status")
    val status: String
)