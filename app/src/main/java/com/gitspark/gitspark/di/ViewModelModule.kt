package com.gitspark.gitspark.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gitspark.gitspark.ui.base.ViewModelFactory
import com.gitspark.gitspark.ui.login.LoginViewModel
import com.gitspark.gitspark.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}