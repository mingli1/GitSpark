package com.gitspark.gitspark.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.gitspark.gitspark.BuildConfig
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences
            = context.getSharedPreferences(BuildConfig.PREFERENCES, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()
}