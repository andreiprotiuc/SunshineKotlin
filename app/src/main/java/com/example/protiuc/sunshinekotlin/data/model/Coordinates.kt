package com.example.protiuc.sunshinekotlin.data.model

import com.google.gson.annotations.SerializedName


data class Coordinates(
        @SerializedName("lon")
        var longitude: Double,
        @SerializedName("lat")
        var latitude: Double
)