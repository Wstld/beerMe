package com.example.studentbeer.viewmodel


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentbeer.R

import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.TempSingleTon
import com.example.studentbeer.data.models.BarModel

import com.example.studentbeer.data.models.UserReviewModel
import com.example.studentbeer.databinding.BarItemBinding
import com.example.studentbeer.databinding.BarListBinding
import com.example.studentbeer.databinding.UserReviewBinding
import com.example.studentbeer.other.adapters.BarRecyclerViewAdapter
import com.example.studentbeer.other.tools.SpacingItemDecoration
import com.google.android.gms.maps.GoogleMap

import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MainActivityViewModel(private val dataRepository: DataRepository) : ViewModel() {
    val currentPos = dataRepository.liveLocationData

 fun getAllBars() = dataRepository.liveBarList
    //clears the map
    fun test(map:GoogleMap) {
        map.clear()
    }

    fun sendUserReview(userReview: UserReviewModel){
        //Connection with firebase
        Log.d("userREVIEW", "sendUserReview: ${userReview.barName} rating:${userReview.userRating} barid:${userReview.barFireBaseId} price:${userReview.userDefinedBeerPrice} ")
    }

    fun getUserReview(context: Context,bar:BarModel){
        val reviewBinding = UserReviewBinding.inflate(LayoutInflater.from(context))

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(reviewBinding.root)
            .setBackground(ColorDrawable(Color.TRANSPARENT))
            .create()

        reviewBinding.apply {
            userPriceInputCardBarTitle.text = bar.barName
            userReviewCardBarTitle.text = bar.barName
            userPriceInputCardBeerPrice.text = bar.beerPrice.toString()
            userPriceInputCardPlusBtn.setOnClickListener {
               val newPrice =  userPriceInputCardBeerPrice.text.toString().toInt() + 1
                userPriceInputCardBeerPrice.text = newPrice.toString()
            }
            userPriceInputCardMinusBtn.setOnClickListener {
                if (userPriceInputCardBeerPrice.text.toString().toInt()>0){
                    val newPrice = userPriceInputCardBeerPrice.text.toString().toInt() -1
                    userPriceInputCardBeerPrice.text = newPrice.toString()
                }
            }
            sendReviewBtn.setOnClickListener {
                sendUserReview(
                    UserReviewModel(
                        barFireBaseId = bar.id,
                        barName = bar.barName,
                        userRating = userReviewCardUserRating.rating.toDouble(),
                        userDefinedBeerPrice = userPriceInputCardBeerPrice.text.toString().toInt()
                    ))
                dialog.dismiss()
            }
        }
            dialog.show()
    }

    fun showDialogOnMarkerClick(context: Context, bar: BarModel) {
        val dialogBinding = BarItemBinding.inflate(LayoutInflater.from(context))

        dialogBinding.apply {
            tvPrice.text = bar.beerPrice.toString()
            tvBarName.text = bar.barName
            tvAddress.text = bar.streetName
            tvOpenHours.text = bar.openHours
            rbBar.rating = bar.rating.toFloat()
        }

        val dialog = MaterialAlertDialogBuilder(context)
            .setView(dialogBinding.root)
            .setBackground(ColorDrawable(Color.WHITE))
            .create()
        dialog.show()
    }

    fun dialogWindow(context: Context) {
        val barList = BarListBinding.inflate(LayoutInflater.from(context))
        barList.rvBar.apply {
            addItemDecoration(SpacingItemDecoration(10, 10, 10, 10))
            adapter = BarRecyclerViewAdapter(TempSingleTon.bars, context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }
        val dialog = MaterialAlertDialogBuilder(context).setView(barList.root)
            .setBackground(ColorDrawable(Color.WHITE)).create()
        dialog.show()
    }



}