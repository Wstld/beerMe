package com.example.studentbeer.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.databinding.ActivityMainBinding
import com.example.studentbeer.util.MainActivityViewModelFactory
import com.example.studentbeer.util.UtilInject
import com.example.studentbeer.viewmodel.MainActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(),
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback{
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
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
        binding.mapView.getMapAsync ( this )

        //test onckick via viewmodel
        binding.fabList.setOnClickListener { view ->
            viewModel.test(view.context)
            viewModel.getSomeData(view.context)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY) ?: Bundle().apply {
            putBundle(MAPVIEW_BUNDLE_KEY,this)
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

    override fun onMapReady(map: GoogleMap?) {
        val stockholmLatLng = LatLng(59.313152, 18.075067)
        val stockholmMarker = MarkerOptions().position(stockholmLatLng).title("Stockholm")
        map?.apply {
            addMarker(stockholmMarker)
            moveCamera(CameraUpdateFactory.newLatLng(stockholmLatLng))
            setOnMarkerClickListener {
                moveCamera(CameraUpdateFactory.newLatLngZoom(stockholmLatLng, 10f))
                animateCamera(CameraUpdateFactory.zoomIn())
                onMarkerClick(it)
            }
        }

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

    companion object {
    private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
}
}

