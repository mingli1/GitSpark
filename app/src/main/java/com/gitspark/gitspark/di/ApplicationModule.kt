package com.gitspark.gitspark.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.gitspark.gitspark.BuildConfig
import com.gitspark.gitspark.room.Database
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

    @Provides
    @Singleton
    fun provideDatabase(context: Context) =
        Room.databaseBuilder(context, Database::class.java, BuildConfig.DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideAuthUserDao(database: Database) = database.authUserDao()
}