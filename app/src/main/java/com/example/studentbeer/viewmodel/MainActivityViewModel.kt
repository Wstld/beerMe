package com.example.studentbeer.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel

class MainActivityViewModel():ViewModel() {
    fun test(context: Context){
        Toast.makeText(context, "Klick klick", Toast.LENGTH_SHORT).show()
    }

}