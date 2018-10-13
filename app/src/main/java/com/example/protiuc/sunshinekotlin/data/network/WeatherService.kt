package com.example.protiuc.sunshinekotlin.data.network

import android.arch.lifecycle.LiveData
import com.example.protiuc.sunshinekotlin.data.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Rest API access point
 */
interface WeatherService {
    @GET("weather")
    fun getWeatherForLocation(@Query("q") query: String, @Query("mode") format: String, @Query("units") units: String, @Query("cnt") days: String) : LiveData<ApiResponse<Weather>>
}