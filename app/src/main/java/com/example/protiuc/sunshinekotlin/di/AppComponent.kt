package com.example.protiuc.sunshinekotlin.di

import android.app.Application
import com.example.protiuc.sunshinekotlin.SunshineApp
import javax.inject.Singleton


@Singleton
interface AppComponent {
    interface Builder {

        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(sunshineApp: SunshineApp)
}