package com.example.protiuc.sunshinekotlin.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class City {
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("coord")
    @Expose
    var coord: Coordinates? = null
    @SerializedName("country")
    @Expose
    var country: String? = null
    @SerializedName("population")
    @Expose
    var population: Int? = null
}