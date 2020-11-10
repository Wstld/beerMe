package com.example.studentbeer.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.models.BarModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val dataRepository: DataRepository) : ViewModel() {
   val allBars = mutableListOf<BarModel>()

    fun test(context: Context) {
        Toast.makeText(context, "Klick klick", Toast.LENGTH_SHORT).show()
    }



}