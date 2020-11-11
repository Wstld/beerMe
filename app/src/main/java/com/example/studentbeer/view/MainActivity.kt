package com.example.studentbeer.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.data.models.BarModel
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

class MainActivity : AppCompatActivity(),
    GoogleMap.OnMarkerClickListener{
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var googleMap: GoogleMap
    private var mapReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init viewmodel
        val factory = UtilInject.viewModelFactoryInjection()
        viewModel = ViewModelProvider(this,factory).get(
            MainActivityViewModel::class.java
        )

        //init map
        val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        binding.mapView.onCreate(mapViewBundle)
        binding.mapView.getMapAsync{map ->
            googleMap = map
            mapReady = true
            val list = viewModel.getAllBars().value
            if (list!=null && list.isNotEmpty())
            updateMap(map = map,list)
            if (mapReady) {
                val stockholmLatLng = LatLng(59.311054, 18.030045)
                val stockholmMarker = MarkerOptions().position(stockholmLatLng).title("IT - Högskolan")
                map!!.setMinZoomPreference(15.5f)
                map?.apply {
                    addMarker(stockholmMarker)
                    moveCamera(CameraUpdateFactory.newLatLng(stockholmLatLng))
                }
            }
        }
        //Check for changes in database and updatesmarkers

        viewModel.getAllBars().observe(this,{
            if (it != null&&mapReady){
            updateMap(googleMap,it)}
        })





        //test live update with adding bar.
        binding.fabList.setOnClickListener { view ->
            if(mapReady) viewModel.test(googleMap)
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
    fun updateMap(map: GoogleMap,list:List<BarModel>){
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

    companion object {
        private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    }
}

