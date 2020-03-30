package com.gitspark.gitspark.ui.main.profile

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.base.PaginatedViewState
import javax.inject.Inject

class ProfileFeedViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<ProfileFeedViewState>()
    val pageViewState = MutableLiveData<PaginatedViewState<Event>>()

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
            refreshing = refresh
        ) ?: ProfileFeedViewState(
            loading = reset,
            refreshing = refresh
        )
        if (reset) page = 1
        username?.let { requestEvents(it) }
    }

    private fun requestEvents(username: String) {
        subscribe(eventRepository.getEvents(username, page)) {
            when (it) {
                is EventResult.Success -> {
                    val updatedList = if (page.isFirstPage()) mutableListOf() else pageViewState.value?.items ?: mutableListOf()
                    updatedList.addAll(it.value.value)

                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false
                    )
                    pageViewState.value = pageViewState.value?.copy(
                        items = updatedList,
                        isLastPage = page.isLastPage(it.value.last)
                    ) ?: PaginatedViewState(
                        items = updatedList,
                        isLastPage = page.isLastPage(it.value.last)
                    )
                    if (page < it.value.last) page++
                }
                is EventResult.Failure -> {
                    alert(it.error)
                    viewState.value = viewState.value?.copy(
                        loading = false,
                        refreshing = false
                    )
                }
            }
        }
    }
}