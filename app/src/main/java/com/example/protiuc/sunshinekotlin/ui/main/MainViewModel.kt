package com.example.protiuc.sunshinekotlin.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.protiuc.sunshinekotlin.data.SunshineRepository
import com.example.protiuc.sunshinekotlin.data.database.WeatherEntry
import com.example.protiuc.sunshinekotlin.data.network.Resource
import javax.inject.Inject


class MainViewModel @Inject constructor( val repository: SunshineRepository, val forecast: LiveData<Resource<List<WeatherEntry>>> = repository.getCurrentWeatherForecasts()) : ViewModel()
