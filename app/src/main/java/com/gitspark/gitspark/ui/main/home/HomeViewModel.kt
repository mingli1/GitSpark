package com.gitspark.gitspark.ui.main.home

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import javax.inject.Inject

private const val NUM_RECENT_EVENTS = 3

class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel() {

    val viewState = MutableLiveData<HomeViewState>()

    private var started = false
    private var page = 1

    fun onStart() {
        if (!started) {
            updateViewState(reset = true)
            started = true
        }
    }

    fun onDestroy() {
        started = false
    }

    fun onScrolledToEnd() = updateViewState(scrolled = true)

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false,
        scrolled: Boolean = false
    ) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: HomeViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        if (!scrolled || reset || refresh) requestRecentEvents()
        requestAllEvents()
    }

    private fun requestRecentEvents() {
        subscribe(eventRepository.getEvents(prefsHelper.getAuthUsername(), 1)) {
            when (it) {
                is EventResult.Success -> {
                    val recentEvents = it.value.value.take(NUM_RECENT_EVENTS)
                    viewState.value = viewState.value?.copy(recentEvents = recentEvents)
                }
                is EventResult.Failure -> alert(it.error)
            }
        }
    }

    private fun requestAllEvents() {
        subscribe(eventRepository.getReceivedEvents(prefsHelper.getAuthUsername(), page)) {
            when (it) {
                is EventResult.Success -> {
                    val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.allEvents ?: arrayListOf()
                    updatedList.addAll(it.value.value)

                    viewState.value = viewState.value?.copy(
                        allEvents = updatedList,
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