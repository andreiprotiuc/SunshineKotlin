package com.example.protiuc.sunshinekotlin.data.model

import com.google.gson.annotations.SerializedName

data class City(
        @SerializedName("id")
        var id: Int,
        @SerializedName("name")
        var name: String,
        @SerializedName("coord")
        var coord: Coordinates,
        @SerializedName("country")
        var country: String,
        @SerializedName("population")
        var population: Int
)