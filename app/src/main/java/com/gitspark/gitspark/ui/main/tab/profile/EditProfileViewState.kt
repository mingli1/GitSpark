package com.gitspark.gitspark.ui.main.tab.profile

data class EditProfileViewState(
    val nameText: String = "",
    val emailText: String = "",
    val bioText: String = "",
    val urlText: String = "",
    val hireable: Boolean = false,
    val companyText: String = "",
    val locationText: String = ""
)