package com.example.protiuc.sunshinekotlin.data.model

import com.google.gson.annotations.SerializedName


data class WeatherEntry(
        @SerializedName("dt")
        var dt: Int,
        @SerializedName("temp")
        var temp: Temp,
        @SerializedName("pressure")
        var pressure: Double,
        @SerializedName("humidity")
        var humidity: Double,
        @SerializedName("weather")
        var weatherDetails: List<WeatherDetails>,
        @SerializedName("speed")
        var speed: Double,
        @SerializedName("deg")
        var deg: Double,
        @SerializedName("clouds")
        var clouds: Int
)