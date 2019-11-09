package com.gitspark.gitspark.ui.main.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class UserSharedViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    val userData = MutableLiveData<User>()

    fun updateUserData(username: String) {
        subscribe(userRepository.getUser(username)) {
            if (it is UserResult.Success) userData.value = it.value
        }
    }
}