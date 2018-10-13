package com.example.protiuc.sunshinekotlin.data.model

import com.google.gson.annotations.SerializedName

data class Weather (
    @SerializedName("city")
    var city: City?,
    @SerializedName("cod")
    var cod: String?,
    @SerializedName("message")
    var message: Double?,
    @SerializedName("cnt")
    var cnt: Int?,
    @SerializedName("list")
    var list: List<WeatherEntry>? = null)