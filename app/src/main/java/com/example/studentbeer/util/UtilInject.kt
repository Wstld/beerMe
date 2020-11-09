package com.example.studentbeer.util

import com.example.studentbeer.data.DataRepository

object UtilInject {

    fun viewModelFactoryInjection():MainActivityViewModelFactory{
        val repository = DataRepository()
        return MainActivityViewModelFactory(repository)
    }
}