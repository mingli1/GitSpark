package com.gitspark.gitspark.di

import com.gitspark.gitspark.helper.RetrofitHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideRetrofitHelper() = RetrofitHelper()
}