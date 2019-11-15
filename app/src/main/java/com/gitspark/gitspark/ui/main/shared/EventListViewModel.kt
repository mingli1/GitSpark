package com.gitspark.gitspark.ui.main.shared

import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import javax.inject.Inject

enum class EventListType {
    None,
    PublicEvents,
    RepoEvents
}

class EventListViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : BaseViewModel(), UserProfileNavigator {

    val viewState = MutableLiveData<ListViewState<Event>>()
    val navigateToProfileAction = SingleLiveEvent<String>()

    private var resumed = false
    private var page = 1

    private var type = EventListType.None
    private var args = ""

    fun onResume(type: EventListType, args: String) {
        if (type == EventListType.None) return
        this.type = type
        this.args = args

        if (!resumed) {
            updateViewState(reset = true)
            resumed = true
        }
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onScrolledToEnd() = updateViewState()

    fun onDestroy() {
        resumed = false
    }

    override fun onUserSelected(username: String) {
        navigateToProfileAction.value = username
    }

    private fun updateViewState(reset: Boolean = false, refresh: Boolean = false) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        ) ?: ListViewState(
            loading = reset,
            refreshing = refresh,
            updateAdapter = false
        )
        if (reset) page = 1
        requestData()
    }

    private fun requestData() {
        when (type) {
            EventListType.None -> return
            EventListType.PublicEvents -> requestPublicEvents()
            EventListType.RepoEvents -> return
        }
    }

    private fun requestPublicEvents() {
        subscribe(eventRepository.getPublicEvents(page = page)) {
            onEventResult(it)
        }
    }

    private fun onEventResult(it: EventResult<Page<Event>>) {
        when (it) {
            is EventResult.Success -> onEventSuccess(it.value.value, it.value.last)
            is EventResult.Failure -> onEventFailure(it.error)
        }
    }

    private fun onEventSuccess(eventsToAdd: List<Event>, last: Int) {
        val updatedList = if (page.isFirstPage()) arrayListOf() else viewState.value?.list ?: arrayListOf()
        updatedList.addAll(eventsToAdd)

        viewState.value = viewState.value?.copy(
            list = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true,
            loading = false,
            refreshing = false
        ) ?: ListViewState(
            list = updatedList,
            isLastPage = page.isLastPage(last),
            updateAdapter = true,
            loading = false,
            refreshing = false
        )
        if (page < last) page++
    }

    private fun onEventFailure(error: String) {
        alert(error)
        viewState.value = viewState.value?.copy(updateAdapter = false, loading = false, refreshing = false)
            ?: ListViewState(updateAdapter = false, loading = false, refreshing = false)
    }
}