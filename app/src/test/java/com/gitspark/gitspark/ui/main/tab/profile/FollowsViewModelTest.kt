package com.gitspark.gitspark.ui.main.tab.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.Page
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val authUser = AuthUser().apply {
    followers = 12
    following = 100
}
private val followsSuccess = UserResult.Success(Page(
    next = 3,
    last = 10,
    value = listOf(User())
))

class FollowsViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FollowsViewModel
    @RelaxedMockK private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = FollowsViewModel(userRepository)
    }

    @Test
    fun shouldGetCachedUserDataAndGetFollowersOnResume() {
        viewModel.onResume()
        verify { userRepository.getCurrentUserData() }
        verify { userRepository.getAuthUserFollowers(any()) }
    }

    @Test
    fun shouldUpdateViewStateOnRefresh() {
        viewModel.onRefresh()

        assertThat(viewState()).isEqualTo(FollowsViewState(
            loading = true,
            refreshing = true,
            updateAdapter = false,
            followState = FollowState.Followers
        ))
        verify { userRepository.getAuthUserFollowers(any()) }
    }

    @Test
    fun shouldUpdateViewStateOnUserDataRetrieved() {
        viewModel.onUserDataRetrieved(authUser)
        assertThat(viewState()).isEqualTo(FollowsViewState(
            followState = FollowState.Followers,
            totalFollowers = 12,
            totalFollowing = 100
        ))
    }

    @Test
    fun shouldUpdateViewStateOnScrolledToEndWithoutReset() {
        viewModel.onScrolledToEnd()
        assertThat(viewState()).isEqualTo(FollowsViewState(
            loading = false,
            refreshing = false,
            updateAdapter = false,
            followState = FollowState.Followers
        ))
        verify { userRepository.getAuthUserFollowers(any()) }
    }

    @Test
    fun shouldUpdateViewStateWithSwappedFollowState() {
        viewModel.onFollowsSwitchClicked()
        assertThat(viewState()).isEqualTo(FollowsViewState(
            loading = true,
            refreshing = false,
            updateAdapter = false,
            followState = FollowState.Following
        ))
        verify { userRepository.getAuthUserFollowing(any()) }
    }

    @Test
    fun shouldUpdateViewStateWithFollowersSuccess() {
        every { userRepository.getAuthUserFollowers(any()) } returns
                Observable.just(followsSuccess)

        viewModel.onScrolledToEnd()

        assertThat(viewState()).isEqualTo(FollowsViewState(
            data = followsSuccess.value.value,
            loading = false,
            refreshing = false,
            isLastPage = false,
            currPage = 1,
            updateAdapter = true
        ))
    }

    @Test
    fun shouldUpdateViewStateWithFollowersFailure() {
        every { userRepository.getAuthUserFollowers(any()) } returns
                Observable.just(UserResult.Failure("failure"))

        viewModel.onScrolledToEnd()

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState()).isEqualTo(FollowsViewState(
            loading = false,
            updateAdapter = false
        ))
    }

    @Test
    fun shouldUpdateViewStateWithFollowingSuccess() {
        every { userRepository.getAuthUserFollowing(any()) } returns
                Observable.just(followsSuccess)

        viewModel.onFollowsSwitchClicked()

        assertThat(viewState()).isEqualTo(FollowsViewState(
            data = followsSuccess.value.value,
            loading = false,
            refreshing = false,
            isLastPage = false,
            currPage = 1,
            updateAdapter = true,
            followState = FollowState.Following
        ))
    }

    @Test
    fun shouldUpdateViewStateWithFollowingFailure() {
        every { userRepository.getAuthUserFollowing(any()) } returns
                Observable.just(UserResult.Failure("failure"))

        viewModel.onFollowsSwitchClicked()

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState()).isEqualTo(FollowsViewState(
            loading = false,
            updateAdapter = false,
            followState = FollowState.Following
        ))
    }

    @Test
    fun shouldNavigateToState() {
        viewModel.navigateToState(FollowState.Followers)
        assertThat(viewState()).isEqualTo(FollowsViewState(
            loading = true,
            refreshing = false,
            updateAdapter = false,
            followState = FollowState.Followers
        ))
    }

    @Test
    fun shouldNavigateToProfileOnUserCardClicked() {
        viewModel.onUserClicked("mingli1")
        assertThat(viewModel.navigateToProfile.value).isEqualTo("mingli1")
    }

    private fun viewState() = viewModel.viewState.value!!
}