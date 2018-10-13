package com.example.protiuc.sunshinekotlin.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.example.protiuc.sunshinekotlin.AppExecutors
import com.example.protiuc.sunshinekotlin.data.SunshineRepository
import com.example.protiuc.sunshinekotlin.data.database.SunshineDatabase
import com.example.protiuc.sunshinekotlin.data.database.WeatherDao
import com.example.protiuc.sunshinekotlin.data.network.WeatherService
import com.example.protiuc.sunshinekotlin.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideWeatherService(): WeatherService{
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        val client : OkHttpClient = OkHttpClient.Builder().apply {
            this.addNetworkInterceptor(interceptor)
        }.build()

        return Retrofit.Builder()
                .baseUrl("https://andfun-weather.udacity.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(WeatherService::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(app: Application): SunshineDatabase {
        return Room.databaseBuilder(app, SunshineDatabase::class.java, "weather").build()
    }

    @Singleton
    @Provides
    fun provideWeatherDao(db: SunshineDatabase): WeatherDao {
        return db.weatherDao()
    }

    @Singleton
    @Provides
    fun provideRepository(db: SunshineDatabase, weatherService: WeatherService, executors: AppExecutors): SunshineRepository {
        return SunshineRepository(weatherService, db.weatherDao(), executors)
    }
}