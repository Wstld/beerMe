package com.example.studentbeer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.studentbeer.databinding.ActivityMainBinding
import com.google.android.gms.maps.MapView

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}