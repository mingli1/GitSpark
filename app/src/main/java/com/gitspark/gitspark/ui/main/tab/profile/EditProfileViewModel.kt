package com.gitspark.gitspark.ui.main.tab.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.api.model.ApiEditProfileRequest
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class EditProfileViewModel @Inject constructor() : BaseViewModel() {

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
}