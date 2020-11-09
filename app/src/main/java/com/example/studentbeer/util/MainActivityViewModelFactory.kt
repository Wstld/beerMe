package com.example.studentbeer.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studentbeer.data.DataRepository
import com.example.studentbeer.viewmodel.MainActivityViewModel

class MainActivityViewModelFactory(private val dataRepository: DataRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(dataRepository) as T
    }
}