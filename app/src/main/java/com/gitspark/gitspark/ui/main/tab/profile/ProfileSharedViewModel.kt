package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gitspark.gitspark.model.AuthUser
import javax.inject.Inject

class ProfileSharedViewModel @Inject constructor() : ViewModel() {

    val userData = MutableLiveData<AuthUser>()
}