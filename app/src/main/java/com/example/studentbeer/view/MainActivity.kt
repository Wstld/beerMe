package com.example.studentbeer.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.studentbeer.R

import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.TempSingleTon
import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.data.models.LocationModel
import com.example.studentbeer.databinding.ActivityMainBinding
import com.example.studentbeer.util.MainActivityViewModelFactory
import com.example.studentbeer.util.UtilInject
import com.example.studentbeer.viewmodel.MainActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.BubbleIconFactory
import com.google.maps.android.ui.IconGenerator
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.math.log
import kotlin.math.pow

const val LOCATION_REQUEST = 1
class MainActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var googleMap: GoogleMap
    private var location: LocationModel = LocationModel(59.31159,18.030053)
    private var mapReady = false
    private var userLocationZoomSet = false





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init viewmodel
        val factory = UtilInject.viewModelFactoryInjection(this.application)
        viewModel = ViewModelProvider(this,factory).get(
            MainActivityViewModel::class.java
        )

        binding.fabList.setOnClickListener {
             viewModel.dialogWindow(this,binding.fabList,binding.fabEndNav,googleMap)
        }


        //mapbundel
        val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        binding.mapView.onCreate(mapViewBundle)

        //endnavigation button
        binding.fabEndNav.setOnClickListener {
            viewModel.enroutToBar = null
            viewModel.isNavClicked = false
            userLocationZoomSet = false
            binding.fabList.visibility = View.VISIBLE
            it.visibility = View.GONE
            initMap()
        }


    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().apply {
            putBundle(MAPVIEW_BUNDLE_KEY, this)
        }
        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        if (viewModel.visistedBar!=null){
            runBlocking {  viewModel.getUserReview(this@MainActivity) }
        }
        //restores not set on position value on restart.
        userLocationZoomSet = false
        initMap()
    }



    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()

    }
    override fun onMarkerClick(marker: Marker?): Boolean {
        if (marker != null) {
            val currentBar = marker.tag as BarModel
            viewModel.showDialogOnMarkerClick(this,currentBar,googleMap,binding.fabEndNav,binding.fabList)
            }
        return false
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()

    }

    // happens after we have asked for permission.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //catches our request code callback.
        when(requestCode){
            LOCATION_REQUEST -> {
                //if user accepts request the observer will fire. Otherwise the initMap function continues.
                if (!grantResults.contains(-1)) {
                    setLocationListner()
                }
            }
        }
    }

    //ask for premission if not granted

    private fun askForlocationPremission() {
       when {
           //if we have location permission this will start observer.
           isPermissionsGranted() -> {
               setLocationListner()
           }
           //if not we'll ask for it.
            else  -> {
                     ActivityCompat.requestPermissions(
                    this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                     LOCATION_REQUEST)
            }
        }
    }

    //checks if location permission is given and returns true or false.
    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )== PackageManager.PERMISSION_GRANTED


    fun updateMap(map: GoogleMap, list:List<BarModel>){
        map.clear()
        list.forEach {
                bar ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(bar.latitude,bar.longitude))
                    .title(bar.barName)
                    .icon(BitmapDescriptorFactory.fromBitmap(
                        IconGenerator(this).makeIcon("${bar.beerPrice}kr")
                    ))

            ).tag = bar
        }
        map.setOnMarkerClickListener(this)
    }


    fun updateMapPosition(pos:LocationModel){
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(pos.lat,pos.long),16.0f
                ))
    }

    fun initMap(){
        binding.mapView.getMapAsync{map ->
            googleMap = map
            mapReady = true
            viewModel.getAllBars().observe(this) {
                if(it.isNotEmpty()){
                    updateMap(map=map, list = it)
                }
            }
            //sets default map camera position
            updateMapPosition(location)

            //ask for location grant, if user agrees to this setlocation() observes user location and updates position of camera to this once.
            askForlocationPremission()

            //If bar navigation was not completed.
            if (viewModel.enroutToBar!=null){
                val directions = viewModel.getDirections(location.lat,location.long,viewModel.enroutToBar!!.latitude,viewModel.enroutToBar!!.longitude)
                val polyline = viewModel.getPolyLine(directions)
                map.addPolyline(polyline)
                binding.fabEndNav.visibility = View.VISIBLE
                binding.fabList.visibility = View.GONE
            }
            //


        }
    }


    @SuppressLint("MissingPermission")
    private fun setLocationListner(){
        if (isPermissionsGranted())
        viewModel.currentPos.observe(this
        ) {
            //observese changes in location and sets location varible to current.
          location = it

            when{
                !userLocationZoomSet ->{

                        updateMapPosition(location)
                        googleMap.isMyLocationEnabled = true
                        //sets notifies observer that location on startup has already been set.
                        userLocationZoomSet = true

                }
                //if navigation is clicked this will fire on location update
                viewModel.isNavClicked ->{

                    if (viewModel.enroutToBar!=null&&viewModel.haversineFormula(location.lat,location.long,viewModel.enroutToBar!!.latitude,viewModel.enroutToBar!!.longitude)<0.04){
                        viewModel.visistedBar = viewModel.enroutToBar
                        viewModel.enroutToBar = null
                        viewModel.isNavClicked = false
                        initMap()
                        binding.fabEndNav.visibility = View.GONE
                        binding.fabList.visibility = View.VISIBLE
                        Toast.makeText(this, R.string.arrived_at_destination, Toast.LENGTH_SHORT).show()
                    }

                }

            }

        }
    }




    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }
}

