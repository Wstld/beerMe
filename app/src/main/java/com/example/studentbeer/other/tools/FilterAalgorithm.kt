package com.example.studentbeer.other.tools

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import kotlin.math.PI
import kotlin.math.cos


// It's not finished.
class FilterAlgorithm() {

    private val earthRadius: Int = 6378

    private fun newLatitude(position: LatLng, distance: Int): Double {
        val meterInDegree = (1 / ((2 * PI / 360) * earthRadius)) / 1000
        return position.latitude + (distance * meterInDegree)
    }

    private fun newLongitude(position: LatLng, distance: Int): Double {
        val meterInDegree = (1 / ((2 * PI / 360) * earthRadius)) / 1000
        return position.longitude + (distance * meterInDegree) / cos(position.latitude * (PI / 180))
    }

    fun getNewLatLng(position: LatLng, distance: Int): String {
        return "${newLatitude(position, distance)}, ${newLongitude(position, distance)}"
    }

    fun calculateTimeBetweenLatLngs(currentPosition: LatLng, destination: LatLng): Float {
        val results: FloatArray = floatArrayOf()
        val humanNormalSpeedInKmPerHour = 4.82
        Location.distanceBetween(
            currentPosition.latitude,
            currentPosition.longitude,
            destination.latitude,
            destination.longitude,
            results
        )
        return results[1]

    }
}