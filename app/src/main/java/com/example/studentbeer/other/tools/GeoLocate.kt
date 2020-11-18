package com.example.studentbeer.other.tools

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList


// It will be used to search after bars. Please do not remove. :)

class GeoLocate(private val activity: Activity, private val searchKeywords: List<String>) {
    private var searchResultList: ArrayList<List<Address>> = ArrayList()
    private val returnSearchResultList: MutableList<String> = mutableListOf()
    private var searchResult: Int = 0

    init {
        searchAfterKeyword()
        getLatLongOfAddresses()
    }

    private fun searchAfterKeyword() {
        val geocoder: Geocoder = Geocoder(activity, Locale.getDefault())
        for (i in searchKeywords.indices) {
            try {
                searchResultList.add(geocoder.getFromLocationName(searchKeywords[i], 10))
            } catch (event: Exception) {
                Log.d("!!!", "Something went wrong!")
            }
        }
        searchResult = searchResultList.size

    }

    private fun getLatLongOfAddresses() {
        searchResultList.forEach {
            it.forEach { itt ->
                returnSearchResultList.add("${itt.latitude}||${itt.longitude}")
            }
        }
    }

    fun getFoundResult(): Int = searchResult

    fun getResultValue(): MutableList<String> = returnSearchResultList

}

