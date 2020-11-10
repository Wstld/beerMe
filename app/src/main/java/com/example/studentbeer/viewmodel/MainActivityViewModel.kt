package com.example.studentbeer.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.models.BarModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val dataRepository: DataRepository) : ViewModel() {

 fun getAllBars() = dataRepository.liveBarList
    //clears the map
    fun test(map:GoogleMap) {
        map.clear()
    }




}