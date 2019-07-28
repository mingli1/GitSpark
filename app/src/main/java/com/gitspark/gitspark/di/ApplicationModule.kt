package com.gitspark.gitspark.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideTestString(): String {
        return "test"
    }
}