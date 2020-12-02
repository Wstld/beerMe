package com.example.studentbeer.other.tools

import android.location.Location
import android.util.Log
import com.example.studentbeer.data.models.BarAndDistanceModel


object FilterAlgorithm {

    fun checkDistance(
        currentLat: Double,
        currentLong: Double,
        destinationLat: Double,
        destinationLong: Double
    ): Boolean {
        val locationA = Location("Point A")
        val locationB = Location("Point B")
        locationA.latitude = currentLat
        locationA.longitude = currentLong
        locationB.latitude = destinationLat
        locationB.longitude = destinationLong
        val distance = locationA.distanceTo(locationB)
        Log.d("!!!", distance.toString())
        if (distance > 2000F) return false
        return true
    }

    fun sortByPrice(list: MutableList<BarAndDistanceModel>): MutableList<BarAndDistanceModel> {
        list.sortBy {
            it.bar.beerPrice
        }
        return list
    }

    fun sortByRating(list: MutableList<BarAndDistanceModel>): MutableList<BarAndDistanceModel> {
        list.sortBy {
            it.bar.rating
        }
        list.reverse()
        return list
    }

    fun sortByName(list: MutableList<BarAndDistanceModel>): MutableList<BarAndDistanceModel> {
        list.sortBy {
            it.bar.barName
        }
        return list
    }

    // inte klar än.
    fun sortByDistance(list: MutableList<BarAndDistanceModel>): MutableList<BarAndDistanceModel> {
        list.sortBy {
            it.distanceInValue
        }
        return list
    }

}
