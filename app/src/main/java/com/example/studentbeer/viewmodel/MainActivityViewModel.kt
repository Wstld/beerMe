package com.example.studentbeer.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.data.models.LocationModel
import com.example.studentbeer.databinding.UserReviewBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame

class MainActivityViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val currentPos = dataRepository.liveLocationData

 fun getAllBars() = dataRepository.liveBarList
    //clears the map
    fun test(map:GoogleMap) {
        map.clear()
    }

    fun getUserReview(context: Context,bar:BarModel){
        val reviewBinding = UserReviewBinding.inflate(LayoutInflater.from(context))
        reviewBinding.apply {
            userPriceInputCardBarTitle.text = bar.barName
            userReviewCardBarTitle.text = bar.barName
            userPriceInputCardBeerPrice.text = bar.beerPrice.toString()
        }
        MaterialAlertDialogBuilder(context)
            .setView(reviewBinding.root)
            .setBackground(ColorDrawable(Color.TRANSPARENT))
            .show()
    }




}