package com.gitspark.gitspark.ui.main.tab.profile

import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class FollowsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesHelper: PreferencesHelper
) : BaseViewModel() {

    fun onResume() {

    }
}