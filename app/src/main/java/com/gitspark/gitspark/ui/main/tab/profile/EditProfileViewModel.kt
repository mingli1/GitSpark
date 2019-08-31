package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiEditProfileRequest
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    val viewState = MutableLiveData<EditProfileViewState>()

    fun fillInitialData(edit: ApiEditProfileRequest) {
        with (edit) {
            viewState.value = EditProfileViewState(
                nameText = name,
                bioText = bio,
                locationText = location,
                emailText = email,
                urlText = url,
                hireable = hireable,
                companyText = company
            )
        }
    }

    fun onNameEdited(name: String) {
        viewState.value = viewState.value?.copy(nameText = name)
    }

    fun onBioEdited(bio: String) {
        viewState.value = viewState.value?.copy(bioText = bio)
    }

    fun onLocationEdited(location: String) {
        viewState.value = viewState.value?.copy(locationText = location)
    }

    fun onEmailEdited(email: String) {
        viewState.value = viewState.value?.copy(emailText = email)
    }

    fun onUrlEdited(url: String) {
        viewState.value = viewState.value?.copy(urlText = url)
    }

    fun onHireabledChecked(hireable: Boolean) {
        viewState.value = viewState.value?.copy(hireable = hireable)
    }

    fun onCompanyEdited(company: String) {
        viewState.value = viewState.value?.copy(companyText = company)
    }

    fun onUpdateProfileClicked() {
        viewState.value = viewState.value?.copy(loading = true)
        viewState.value?.let {
            subscribe(userRepository.updateUser(ApiEditProfileRequest(
                name = it.nameText,
                email = it.emailText,
                url = it.urlText,
                company = it.companyText,
                location = it.locationText,
                hireable = it.hireable,
                bio = it.bioText
            ))) { result -> handleUpdateProfileResult(result) }
        }
    }

    private fun handleUpdateProfileResult(result: UserResult<AuthUser>) {
        when (result) {
            is UserResult.Success -> {

            }
            is UserResult.Failure -> {
                alert(result.error)
                viewState.value = viewState.value?.copy(loading = false)
            }
        }
    }
}