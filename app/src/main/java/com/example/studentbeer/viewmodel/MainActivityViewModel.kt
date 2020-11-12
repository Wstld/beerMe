package com.example.studentbeer.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.data.models.LocationModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame

class MainActivityViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val currentPos = dataRepository.liveLocationData

 fun getAllBars() = dataRepository.liveBarList
    //clears the map
    fun test(map:GoogleMap) {
        map.clear()
    }




}