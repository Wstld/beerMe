package com.example.studentbeer.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.studentbeer.data.DataRepository

class MainActivityViewModel(private val dataRepository: DataRepository):ViewModel() {
    fun test(context: Context){
        Toast.makeText(context, "Klick klick", Toast.LENGTH_SHORT).show()
    }
    //hämtar från data från repositoryt.
    fun getSomeData(context: Context){
        Toast.makeText(context, "${dataRepository.takeSomeData()}", Toast.LENGTH_SHORT).show()
    }

}