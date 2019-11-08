package com.gitspark.gitspark.ui.main.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

class ProfileFeedViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<ProfileFeedViewState>()

    private var resumed = false
    private var page = 1
    private var username: String? = null

    fun onResume(username: String? = null) {
        this.username = username ?: prefsHelper.getAuthUsername()
        onInitialResume()
    }

    fun onDestroy() {
        resumed = false
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onScrolledToEnd() = updateViewState()

    private fun onInitialResume() {
        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false
    ) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: ProfileFeedViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        username?.let { requestEvents(it) }
    }

    private fun requestEvents(username: String) {
        subscribe(eventRepository.getEvents(username, page)) {
            when (it) {
                is EventResult.Success -> {
                    val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.events ?: arrayListOf()
                    updatedList.addAll(it.value.value)

                    viewState.value = viewState.value?.copy(
                        events = updatedList,
                        loading = false,
                        refreshing = false,
                        isLastPage = page.isLastPage(it.value.last),
                        updateAdapter = true
                    )
                    if (page < it.value.last) page++
                }
                is EventResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false,
                        updateAdapter = false
                    )
                }
            }
        }
    }
}