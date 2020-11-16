package com.example.studentbeer.util

import android.app.Application
import com.example.studentbeer.data.DataRepository

object UtilInject {

    fun viewModelFactoryInjection(application: Application):MainActivityViewModelFactory{
        val repository = DataRepository(application)
        return MainActivityViewModelFactory(repository)
    }
}