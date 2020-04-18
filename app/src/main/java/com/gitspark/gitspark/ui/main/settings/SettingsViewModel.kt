package com.gitspark.gitspark.ui.main.settings

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.DarkModeConfig
import com.gitspark.gitspark.helper.DarkModeHelper
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val darkModeHelper: DarkModeHelper
) : BaseViewModel() {

    val currentTheme = MutableLiveData<String>()
    val showThemeSelector = SingleLiveAction()

    fun onThemeSelectClicked() {
        showThemeSelector.call()
    }

    fun onThemeSelected(theme: String) {
        currentTheme.value = theme
        darkModeHelper.setDarkMode(DarkModeConfig.valueOf(theme))
    }
}