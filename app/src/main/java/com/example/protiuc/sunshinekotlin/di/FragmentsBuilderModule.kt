package com.example.protiuc.sunshinekotlin.di

import com.example.protiuc.sunshinekotlin.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
}
