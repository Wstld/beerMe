package com.example.studentbeer.other.tools

import com.example.studentbeer.data.models.BarFinishedModel


object FilterAlgorithm {

    fun sortByPrice(list: MutableList<BarFinishedModel>): MutableList<BarFinishedModel> {
        list.sortBy {
            it.bar.beerPrice
        }
        return list
    }

    fun sortByRating(list: MutableList<BarFinishedModel>): MutableList<BarFinishedModel> {
        list.sortBy {
            it.bar.rating
        }
        list.reverse()
        return list
    }

    fun sortByName(list: MutableList<BarFinishedModel>): MutableList<BarFinishedModel> {
        list.sortBy {
            it.bar.barName
        }
        return list
    }

    // inte klar än.
    fun sortByDistance(list: MutableList<BarFinishedModel>): MutableList<BarFinishedModel> {
        list.sortBy {
            it.distanceInValue
        }
        return list
    }

}
