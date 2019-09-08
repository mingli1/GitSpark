package com.gitspark.gitspark.ui.main.profile

data class EditProfileViewState(
    val loading: Boolean = false,
    val nameText: String = "",
    val emailText: String = "",
    val bioText: String = "",
    val urlText: String = "",
    val hireable: Boolean = false,
    val companyText: String = "",
    val locationText: String = ""
)