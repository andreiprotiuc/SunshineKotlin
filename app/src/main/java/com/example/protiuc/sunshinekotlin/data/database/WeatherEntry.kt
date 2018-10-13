package com.example.protiuc.sunshinekotlin.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "weather", indices = [
    Index(value = arrayOf("date"), unique = true)]
)
class WeatherEntry {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var weatherIconId: Int = 0
        private set
    var date: Date? = null
        private set
    var min: Double = 0.toDouble()
        private set
    var max: Double = 0.toDouble()
        private set
    var humidity: Double = 0.toDouble()
        private set
    var pressure: Double = 0.toDouble()
        private set
    var wind: Double = 0.toDouble()
        private set
    var degrees: Double = 0.toDouble()
        private set

    /**
     * This constructor is used by OpenWeatherJsonParser. When the network fetch has JSON data, it
     * converts this data to WeatherEntry objects using this constructor.
     * @param weatherIconId Image id for weather
     * @param date Date of weather
     * @param min Min temperature
     * @param max Max temperature
     * @param humidity Humidity for the day
     * @param pressure Barometric pressure
     * @param wind Wind speed
     * @param degrees Wind direction
     */
    @Ignore
    constructor(weatherIconId: Int, date: Date, min: Double, max: Double, humidity: Double, pressure: Double, wind: Double, degrees: Double) {
        this.weatherIconId = weatherIconId
        this.date = date
        this.min = min
        this.max = max
        this.humidity = humidity
        this.pressure = pressure
        this.wind = wind
        this.degrees = degrees
    }

    constructor(id: Int, weatherIconId: Int, date: Date, min: Double, max: Double, humidity: Double, pressure: Double, wind: Double, degrees: Double) {
        this.id = id
        this.weatherIconId = weatherIconId
        this.date = date
        this.min = min
        this.max = max
        this.humidity = humidity
        this.pressure = pressure
        this.wind = wind
        this.degrees = degrees
    }
}
