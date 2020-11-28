package com.example.studentbeer.other.tools

import com.example.studentbeer.data.models.BarAndDistanceModel


object FilterAlgorithm {

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
