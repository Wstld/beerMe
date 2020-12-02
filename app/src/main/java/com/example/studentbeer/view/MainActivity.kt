package com.example.studentbeer.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.graphics.set
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.studentbeer.R

import com.example.studentbeer.data.models.BarModel
import com.example.studentbeer.data.models.LocationModel
import com.example.studentbeer.databinding.ActivityMainBinding
import com.example.studentbeer.util.UtilInject
import com.example.studentbeer.viewmodel.MainActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW
import com.google.android.gms.maps.model.BitmapDescriptorFactory.fromBitmap
import com.google.maps.android.ui.IconGenerator
import kotlinx.coroutines.*

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

        //mapbundle
        val mapViewBundle = savedInstanceState?.getBundle(MAPVIEW_BUNDLE_KEY)
        binding.mapView.onCreate(mapViewBundle)

        //endnavigation button
        binding.fabEndNav.setOnClickListener {
            viewModel.enrouteToBar = null
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
        if (viewModel.visitedBar!=null){
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
                val userAllowedLocation = !grantResults.contains(-1)
                if (userAllowedLocation) {
                    setLocationListener()
                } else {
                    startMissingPermissionsActivity()
                }
            }
        }
    }

    private fun startMissingPermissionsActivity() {
        val intent = Intent(this, MissingPermissionsActivity::class.java).apply {
        }
        startActivity(intent)
    }

    private fun askForLocationPermission() {
        val hasLocationPermissions = isPermissionsGranted()
        if (hasLocationPermissions) {
            setLocationListener()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_REQUEST
            )
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
        val warningIcon = IconGenerator(this)
        warningIcon.setStyle(IconGenerator.STYLE_ORANGE)
        warningIcon.setTextAppearance(R.style.Marker)

        val normalIcon = IconGenerator(this)
        list.forEach {
                bar ->

            map.addMarker(
                MarkerOptions().apply {
                    position(LatLng(bar.latitude,bar.longitude))
                    title(bar.barName)
                    if(bar.questionablePrice){
                        icon(fromBitmap(
                            warningIcon.makeIcon("~${bar.beerPrice}kr")
                        )
                        ).alpha(0.5f)
                    }else{
                    icon(fromBitmap(
                        normalIcon.makeIcon("${bar.beerPrice}kr")))
                    }
                }


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
            askForLocationPermission()

            //If bar navigation was not completed.
            if (viewModel.enrouteToBar!=null){
                val directions = viewModel.getDirections(location.lat,location.long,viewModel.enrouteToBar!!.latitude,viewModel.enrouteToBar!!.longitude)
                val polyline = viewModel.getPolyLine(directions)
                map.addPolyline(polyline)
                binding.fabEndNav.visibility = View.VISIBLE
                binding.fabList.visibility = View.GONE
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocationListener(){
        if (isPermissionsGranted())
        viewModel.currentPos.observe(this
        ) {
            //observes changes in location and sets location variable to current.
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

                    if (viewModel.enrouteToBar!=null&&viewModel.haversineFormula(location.lat,location.long,viewModel.enrouteToBar!!.latitude,viewModel.enrouteToBar!!.longitude)<0.04){
                        viewModel.visitedBar = viewModel.enrouteToBar
                        viewModel.enrouteToBar = null
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

