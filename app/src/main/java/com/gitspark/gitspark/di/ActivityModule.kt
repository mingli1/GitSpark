package com.gitspark.gitspark.di

import com.gitspark.gitspark.ui.login.LoginActivity
import com.gitspark.gitspark.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityModule {

    @ContributesAndroidInjector
    internal abstract fun contributesLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun contributesMainActivity(): MainActivity
}