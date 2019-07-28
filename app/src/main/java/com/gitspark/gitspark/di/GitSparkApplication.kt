package com.gitspark.gitspark.di

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class GitSparkApplication : DaggerApplication() {

    private val appComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }
}