package com.gitspark.gitspark.di

import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.kbiakov.codeview.classifier.CodeProcessor

class GitSparkApplication : DaggerApplication() {

    private val appComponent = DaggerApplicationComponent.builder()
        .application(this)
        .build()

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        CodeProcessor.init(this)
        appComponent.inject(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return appComponent
    }
}