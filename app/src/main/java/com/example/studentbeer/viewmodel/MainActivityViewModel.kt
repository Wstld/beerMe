package com.example.studentbeer.viewmodel


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentbeer.R
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.models.BarAndDistanceModel
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.data.models.UserReviewModel
import com.example.studentbeer.data.models.apiResponse.DirectionsModel
import com.example.studentbeer.databinding.BarItemBinding
import com.example.studentbeer.databinding.BarListBinding
import com.example.studentbeer.databinding.UserReviewBinding
import com.example.studentbeer.other.adapters.BarRecyclerViewAdapter
import com.example.studentbeer.other.tools.FilterAlgorithm
import com.example.studentbeer.other.tools.SpacingItemDecoration
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class MainActivityViewModel(private val dataRepository: DataRepository) : ViewModel(),
    AdapterView.OnItemSelectedListener {
    val currentPos = dataRepository.liveLocationData
    var isNavClicked = false
    var visitedBar: BarModel? = null
    var enrouteToBar: BarModel? = null
    var rvBar: RecyclerView? = null
    private var barAndDistanceModelList: MutableList<BarAndDistanceModel>? = null

    fun getDirections(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): DirectionsModel? {
        val start = "$startLat,$startLng"
        val end = "$endLat,$endLng"
        var directionsModel: DirectionsModel? = null

        val directions = GlobalScope.async { dataRepository.getDirections(start, end) }
        runBlocking {
            directionsModel = directions.await()
        }

        return directionsModel
    }

    fun getAllBars() = dataRepository.liveBarList

    private fun getBarAndDistanceList(): MutableList<BarAndDistanceModel> {
        val list: MutableList<BarAndDistanceModel> = mutableListOf()
        getAllBars().value!!.forEach {
            val directions = getDirections(
                currentPos.value!!.lat,
                currentPos.value!!.long,
                it.latitude,
                it.longitude
            )

            if (directions!!.routes.size > 0) {
                val distanceInTime = directions!!.routes[0].legs[0].duration.text
                val distanceInValue = directions.routes[0].legs[0].duration.value
                val currentObject = BarAndDistanceModel(distanceInTime, distanceInValue, it)
                list.add(currentObject)
            }

        }
        return list
    }


    //clears the map
    fun test(map: GoogleMap) {
        map.clear()
    }

    fun sendUserReview(userReview: UserReviewModel) {
        //Connection with firebase
        Log.d(
            "userREVIEW",
            "sendUserReview: ${userReview.barName} rating:${userReview.userRating} barid:${userReview.barFireBaseId} price:${userReview.userDefinedBeerPrice} "
        )
    }

    fun getUserReview(context: Context) {
        val bar = visitedBar
        if (bar != null) {
            val reviewBinding = UserReviewBinding.inflate(LayoutInflater.from(context))

            val dialog =
                MaterialAlertDialogBuilder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setView(reviewBinding.root)
                    .setBackground(ColorDrawable(Color.TRANSPARENT))
                    .create()

            reviewBinding.apply {
                userPriceInputCardBarTitle.text = bar.barName
                userReviewCardBarTitle.text = bar.barName
                userPriceInputCardBeerPrice.text = bar.beerPrice.toString()
                userPriceInputCardPlusBtn.setOnClickListener {
                    val newPrice = userPriceInputCardBeerPrice.text.toString().toInt() + 1
                    userPriceInputCardBeerPrice.text = newPrice.toString()
                }
                userPriceInputCardMinusBtn.setOnClickListener {
                    if (userPriceInputCardBeerPrice.text.toString().toInt() > 0) {
                        val newPrice = userPriceInputCardBeerPrice.text.toString().toInt() - 1
                        userPriceInputCardBeerPrice.text = newPrice.toString()
                    }
                }
                sendReviewBtn.setOnClickListener {
                    sendUserReview(
                        UserReviewModel(
                            barFireBaseId = bar.id,
                            barName = bar.barName,
                            userRating = userReviewCardUserRating.rating.toDouble(),
                            userDefinedBeerPrice = userPriceInputCardBeerPrice.text.toString()
                                .toInt()
                        )
                    )
                    isNavClicked = false
                    visitedBar = null
                    dialog.cancel()

                }
            }
            dialog.setOnDismissListener {
                isNavClicked = false
                visitedBar = null
            }
            dialog.show()
        } else return
    }

    fun showDialogOnMarkerClick(
        context: Context,
        bar: BarModel,
        map: GoogleMap,
        endNavBtn: FloatingActionButton,
        lisBtn: FloatingActionButton
    ) {
        val directions = getDirections(
            currentPos.value!!.lat,
            currentPos.value!!.long,
            bar.latitude,
            bar.longitude
        )
        val dialogBinding = BarItemBinding.inflate(LayoutInflater.from(context))
        val dialog = MaterialAlertDialogBuilder(context)
            .setBackground(ColorDrawable(Color.TRANSPARENT))
            .create()
        dialogBinding.apply {
            tvPrice.text = bar.beerPrice.toString()
            tvBarName.text = bar.barName
            tvAddress.text = bar.streetName
            tvOpenHours.text = bar.openHours
            tvDistance.text = "${directions?.routes?.get(0)?.legs?.get(0)?.duration?.text}"
                ?: "Cant find duration"
            rbBar.rating = bar.rating.toFloat()
            bDirection.setOnClickListener {
                if (currentPos.value != null) {
                    val myPos = LatLng(currentPos.value!!.lat, currentPos.value!!.long)
                    val polyLine = getPolyLine(directions)
                    if (polyLine != null) map.addPolyline(polyLine)
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(myPos, 16f)
                    )
                    endNavBtn.visibility = View.VISIBLE
                    lisBtn.visibility = View.GONE
                    isNavClicked = true
                    enrouteToBar = bar
                    dialog.dismiss()
                }
            }
        }
        dialog.setView(dialogBinding.root)
        dialog.show()
    }

    fun getPolyLine(directions: DirectionsModel?): PolylineOptions? {
        if (directions != null) {
            val polylineOptions = PolylineOptions()
            val latLng = mutableListOf<List<LatLng>>()
            directions.routes[0].legs[0].steps.forEach {
                latLng.add(PolyUtil.decode(it.polyline.points))
            }
            latLng.forEach {
                polylineOptions.addAll(it)
            }
            return polylineOptions
        } else return null

    }

    fun dialogWindow(
        context: Context,
        listBtn: FloatingActionButton,
        endNavBtn: FloatingActionButton,
        map: GoogleMap
    ) {
        barAndDistanceModelList = getBarAndDistanceList()
        val dialog =
            MaterialAlertDialogBuilder(context).setBackground(ColorDrawable(Color.TRANSPARENT)).create()
        val barList = BarListBinding.inflate(LayoutInflater.from(context))
        rvBar = barList.rvBar
        val filterAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            context,
            R.array.filters,
            R.layout.spinner_item
        )
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        barList.bFilter.adapter = filterAdapter
        barList.rvBar.apply {
            addItemDecoration(SpacingItemDecoration(10, 10, 10, 10))
            adapter = BarRecyclerViewAdapter(
                barAndDistanceModelList!!,
                context,
                onClickedNavBtn = { bar ->
                    onRecyclerButtonClick(
                        bar,
                        dialog = dialog,
                        endNavBtn,
                        listBtn,
                        map
                    )
                }
            )
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        }
        dialog.setView(barList.root)
        dialog.show()
        barList.bFilter.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> {

            }
            1 -> {
                barAndDistanceModelList = FilterAlgorithm.sortByName(barAndDistanceModelList!!)
                rvBar?.adapter?.notifyDataSetChanged()
            }
            2 -> {
                barAndDistanceModelList = FilterAlgorithm.sortByPrice(barAndDistanceModelList!!)
                rvBar?.adapter?.notifyDataSetChanged()
            }
            3 -> {
                barAndDistanceModelList = FilterAlgorithm.sortByRating(barAndDistanceModelList!!)
                rvBar?.adapter?.notifyDataSetChanged()
            }
            4 -> {
                barAndDistanceModelList = FilterAlgorithm.sortByDistance(barAndDistanceModelList!!)
                rvBar?.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Log.d("!!!", "nej")
    }

    fun onRecyclerButtonClick(
        bar: BarModel,
        dialog: androidx.appcompat.app.AlertDialog,
        endNavBtn: FloatingActionButton,
        lisBtn: FloatingActionButton,
        map: GoogleMap
    ) {
        lisBtn.visibility = View.GONE
        endNavBtn.visibility = View.VISIBLE
        enrouteToBar = bar
        isNavClicked = true
        val directions = getDirections(
            currentPos.value!!.lat,
            currentPos.value!!.long,
            bar.latitude,
            bar.longitude
        )
        val polyline = getPolyLine(directions)
        map.addPolyline(polyline)
        dialog.cancel()
    }


    //returns distance between two latLng in km
    fun haversineFormula(fromLat: Double, fromLong: Double, toLat: Double, toLong: Double): Double {
        val earthRad = 6372.8
        val dLat = Math.toRadians(toLat - fromLat)
        val dLon = Math.toRadians(toLong - fromLong)

        val a = Math.pow(Math.sin(dLat / 2), 2.toDouble()) + Math.pow(
            Math.sin(dLon / 2),
            2.toDouble()
        ) * Math.cos(fromLat) * Math.cos(toLat)
        val c = 2 * Math.asin(Math.sqrt(a))
        return earthRad * c
    }


}