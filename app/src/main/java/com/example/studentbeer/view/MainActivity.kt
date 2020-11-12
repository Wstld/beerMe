package com.example.studentbeer.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

import com.example.studentbeer.data.DataRepository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log
const val LOCATION_REQUEST = 1
class MainActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener{
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var googleMap: GoogleMap
    private var location: LocationModel? = null
    private var locationPremission:Boolean = false
    private var mapReady = false


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askForlocationPremission()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init viewmodel
        val factory = UtilInject.viewModelFactoryInjection(this.application)
        viewModel = ViewModelProvider(this,factory).get(
            MainActivityViewModel::class.java
        )

        viewModel.getAllBars().observe(this,{
            if (it != null&&mapReady){
                updateMap(googleMap,it)}
        })
        viewModel.currentPos.observe(this,{
            when(location){
                null -> {
                    updateMapPosition(it)
                    googleMap.isMyLocationEnabled = true
                }
                else -> location = it
            }

        })

        //init map
        val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync{map ->
            googleMap = map
            mapReady = true
            val list = viewModel.getAllBars().value
            if (list!=null && list.isNotEmpty())
            updateMap(map = map,list)


  /*          if (mapReady) {
                val stockholmLatLng = LatLng(59.311054, 18.030045)
                val stockholmMarker = MarkerOptions().position(stockholmLatLng).title("IT - Högskolan")
                map!!.setMinZoomPreference(15.5f)
                map?.apply {
                    addMarker(stockholmMarker)
                    moveCamera(CameraUpdateFactory.newLatLng(stockholmLatLng))
                }
            }*/
        }
        //Check for changes in database and updatesmarkers








        //test live update with adding bar.




    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().apply {
            putBundle(MAPVIEW_BUNDLE_KEY, this)
        }
        binding.mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()

    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()

    }



    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        return false
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        binding.mapView.onLowMemory()
        super.onLowMemory()
    }

    //sets
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            LOCATION_REQUEST -> {
                askForlocationPremission()
            }
            else -> {Toast.makeText(this, "DID NOT ACCEPT", Toast.LENGTH_SHORT).show()}
        }
    }

    //ask for premission if not granted

    private fun askForlocationPremission() {
        when{
            isPermissionsGranted() -> {
                if(!locationPremission)
                {
                locationPremission = true
                }
            }
            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }

    //returns true or false
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

            )
        }
    }
    fun updateMapPosition(pos:LocationModel){
        if (mapReady&&locationPremission){
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(pos.lat,pos.long),16.0f
                ))
            location = pos
            //viewModel.currentPos.stopLocationUpdates()
        }
    }


    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }
}

