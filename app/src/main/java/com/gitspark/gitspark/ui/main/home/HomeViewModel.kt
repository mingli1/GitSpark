package com.gitspark.gitspark.ui.main.home

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Event
import com.gitspark.gitspark.model.isFirstPage
import com.gitspark.gitspark.model.isLastPage
import com.gitspark.gitspark.repository.EventRepository
import com.gitspark.gitspark.repository.EventResult
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.ui.base.BaseViewModel
import com.gitspark.gitspark.ui.base.PaginatedViewState
import com.gitspark.gitspark.ui.livedata.SingleLiveAction
import com.gitspark.gitspark.ui.livedata.SingleLiveEvent
import com.gitspark.gitspark.ui.main.shared.EventListType
import com.gitspark.gitspark.ui.nav.UserProfileNavigator
import javax.inject.Inject

internal const val NUM_RECENT_EVENTS = 2

class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val prefsHelper: PreferencesHelper
) : BaseViewModel(), UserProfileNavigator {

    val viewState = MutableLiveData<HomeViewState>()
    val pageViewState = MutableLiveData<PaginatedViewState<Event>>()
    val userMediator = MediatorLiveData<AuthUser>()
    val logoutConfirmationAction = SingleLiveAction()
    val navigateToLoginAction = SingleLiveAction()
    val navigateToUserProfile = SingleLiveEvent<String>()
    val navigateToEventList = SingleLiveEvent<EventListType>()
    val navigateToSettings = SingleLiveAction()

    private var started = false
    private var page = 1
    private var user: AuthUser? = null

    fun onStart() {
        val userData = userRepository.getCurrentUserData()
        userMediator.addSource(userData) { userMediator.value = it }
    }

    fun onUserDataLoaded(user: AuthUser) {
        this.user = user
        if (!started) {
            updateViewState(reset = true)
            started = true
        }
    }

    fun onRefresh() = updateViewState(reset = true, refresh = true)

    fun onDestroy() {
        started = false
    }

    fun onScrolledToEnd() = updateViewState(scrolled = true)

    fun onLogoutClicked() = logoutConfirmationAction.call()

    fun onLogoutConfirmed() {
        subscribe(userRepository.clearUserData(),
            {
                prefsHelper.onSignOut()
                navigateToLoginAction.call()
            },
            { alert("Failed to sign out.") }
        )
    }

    fun onPublicEventsClicked() {
        navigateToEventList.value = EventListType.PublicEvents
    }

    fun onSettingsClicked() = navigateToSettings.call()

    override fun onUserSelected(username: String) {
        navigateToUserProfile.value = username
    }

    private fun updateViewState(
        reset: Boolean = false,
        refresh: Boolean = false,
        scrolled: Boolean = false
    ) {
        viewState.value = viewState.value?.copy(
            loading = reset,
            refreshing = refresh,
            fullName = user?.name ?: "",
            username = user?.login ?: "",
            avatarUrl = user?.avatarUrl ?: ""
        ) ?: HomeViewState(
            loading = reset,
            refreshing = refresh,
            fullName = user?.name ?: "",
            username = user?.login ?: "",
            avatarUrl = user?.avatarUrl ?: ""
        )
        if (reset) page = 1
        if (!scrolled || reset || refresh) requestRecentEvents()
        requestAllEvents()
    }

    private fun requestRecentEvents() {
        subscribe(eventRepository.getEvents(prefsHelper.getAuthUsername(), 1)) {
            when (it) {
                is EventResult.Success -> viewState.value = viewState.value?.copy(recentEvents = it.value.value)
                is EventResult.Failure -> alert(it.error)
            }
        }
    }

    private fun requestAllEvents() {
        subscribe(eventRepository.getReceivedEvents(prefsHelper.getAuthUsername(), page)) {
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