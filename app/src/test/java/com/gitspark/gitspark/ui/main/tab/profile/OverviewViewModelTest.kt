package com.gitspark.gitspark.ui.main.tab.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gitspark.gitspark.helper.ContributionsHelper
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.model.GitHubPlan
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.repository.UserRepository
import com.gitspark.gitspark.repository.UserResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private val user = User(name = "Ming Li")

class OverviewViewModelTest {

    @Rule @JvmField val liveDataRule = InstantTaskExecutorRule()

    private lateinit var viewModel: OverviewViewModel
    @RelaxedMockK private lateinit var userRepository: UserRepository
    @RelaxedMockK private lateinit var contributionsHelper: ContributionsHelper

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = OverviewViewModel(userRepository, contributionsHelper)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun shouldGetAuthUserDataOnResume() {
        viewModel.onResume()
        verify { userRepository.getCurrentUserData() }
    }

    @Test
    fun shouldUpdateViewStateWithUserDataOnResume() {
        viewModel.onResume(username = "username", user = user)
        assertThat(viewState().nameText).isEqualTo("Ming Li")
    }

    @Test
    fun shouldCheckIfFollowingWhenUsernameExistsOnResume() {
        viewModel.onResume("username")
        verify { userRepository.isFollowing("username") }
    }

    @Test
    fun shouldUpdateWithNewUserIfExpiredSuccess() {
        every { userRepository.isUserCacheExpired(any()) } returns true
        every { userRepository.getAuthUser() } returns
                Observable.just(UserResult.Success(getNewAuthUser()))
        every { userRepository.cacheUserData(any()) } returns Completable.complete()

        viewModel.onCachedUserDataRetrieved(getAuthUser())

        assertThat(viewState().nameText).isEqualTo(getNewAuthUser().name)
    }

    @Test
    fun shouldUpdateWithOldUserIfCannotCacheData() {
        every { userRepository.isUserCacheExpired(any()) } returns true
        every { userRepository.getAuthUser() } returns
                Observable.just(UserResult.Success(getNewAuthUser()))
        every { userRepository.cacheUserData(any()) } returns Completable.error(Throwable())

        viewModel.onCachedUserDataRetrieved(getAuthUser())

        assertThat(viewModel.alertAction.value).isEqualTo("Failed to cache user data.")
        assertThat(viewState().nameText).isEqualTo(getAuthUser().name)
    }

    @Test
    fun shouldUpdateWithOldUserIfFailedToGetNewData() {
        every { userRepository.isUserCacheExpired(any()) } returns true
        every { userRepository.getAuthUser() } returns
                Observable.just(UserResult.Failure("failure"))

        viewModel.onCachedUserDataRetrieved(getAuthUser())

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState().nameText).isEqualTo(getAuthUser().name)
    }

    @Test
    fun shouldUpdatedWithCachedUser() {
        every { userRepository.isUserCacheExpired(any()) } returns false
        every { userRepository.getContributionsSvg(any()) } returns
                Observable.just(UserResult.Success(""))

        viewModel.onCachedUserDataRetrieved(getAuthUser())

        assertThat(viewState().nameText).isEqualTo("Steven")
        assertThat(viewState().usernameText).isEqualTo("orz39")
        assertThat(viewState().avatarUrl).isEqualTo("avatarUrl")
        assertThat(viewState().bioText).isEqualTo("bio")
        assertThat(viewState().locationText).isEqualTo("NJ")
        assertThat(viewState().emailText).isEqualTo("orz39@gmail.com")
        assertThat(viewState().companyText).isEqualTo("Google")
        assertThat(viewState().numFollowers).isEqualTo(999)
        assertThat(viewState().numFollowing).isEqualTo(999)
        assertThat(viewState().planName).isEqualTo("plan")
        assertThat(viewState().createdDate).isEqualTo("01-14-2008 04:33:35")
    }

    @Test
    fun shouldNavigateToFollowsTabOnClicked() {
        viewModel.onFollowsFieldClicked(FollowState.Following)
        assertThat(viewModel.navigateToFollowsAction.value).isEqualTo(FollowState.Following)
    }

    @Test
    fun shouldRequestAuthUserWhenUsernameNullOnRefresh() {
        viewModel.viewState.value = OverviewViewState()

        viewModel.onRefresh()

        assertThat(viewState().refreshing).isTrue()
        verify { userRepository.getAuthUser() }
    }

    @Test
    fun shouldCallRefreshWhenUsernameExistsOnRefresh() {
        viewModel.onResume(username = "test")
        viewModel.onRefresh()
        assertThat(viewModel.refreshAction.value).isNotNull
    }

    @Test
    fun shouldUpdateViewStateOnUserDataRefreshed() {
        viewModel.onUserDataRefreshed(getUser())
        assertThat(viewState().nameText).isEqualTo("Steven")
    }

    @Test
    fun shouldUpdateViewStateOnFollowing() {
        every { userRepository.isFollowing(any()) } returns
                Completable.complete()
        viewModel.onResume(username = "username")
        assertThat(viewState().isFollowing).isTrue()
    }

    @Test
    fun shouldUpdateViewStateOnNotFollowing() {
        every { userRepository.isFollowing(any()) } returns
                Completable.never()
        viewModel.onResume(username = "username")
        assertThat(viewState().isFollowing).isFalse()
    }

    @Test
    fun shouldUnfollowUserOnFollowsButtonClicked() {
        viewModel.viewState.value = OverviewViewState()
        viewModel.username = "username"
        every { userRepository.unfollowUser(any()) } returns
                Completable.complete()

        viewModel.onFollowsButtonClicked(true)

        verify { userRepository.unfollowUser("username") }
        assertThat(viewModel.alertAction.value).isEqualTo("Unfollowed username. Note: it may take some time for numbers to update.")
        assertThat(viewState().isFollowing).isFalse()
    }

    @Test
    fun shouldFollowUserOnFollowsButtonClicked() {
        viewModel.viewState.value = OverviewViewState()
        viewModel.username = "username"
        every { userRepository.followUser(any()) } returns
                Completable.complete()

        viewModel.onFollowsButtonClicked(false)

        verify { userRepository.followUser("username") }
        assertThat(viewModel.alertAction.value).isEqualTo("Followed username. Note: it may take some time for numbers to update.")
        assertThat(viewState().isFollowing).isTrue()
    }

    private fun getAuthUser() = AuthUser().apply {
        name = "Steven"
        login = "orz39"
        avatarUrl = "avatarUrl"
        bio = "bio"
        location = "NJ"
        email = "orz39@gmail.com"
        company = "Google"
        followers = 999
        following = 999
        plan = GitHubPlan(planName = "plan")
        createdAt = "2008-01-14T04:33:35Z"
    }

    private fun getUser() = User().apply {
        name = "Steven"
        login = "orz39"
        avatarUrl = "avatarUrl"
        bio = "bio"
        location = "NJ"
        email = "orz39@gmail.com"
        company = "Google"
        followers = 999
        following = 999
        createdAt = "2008-01-14T04:33:35Z"
    }

    private fun getNewAuthUser() = getAuthUser().copy().apply {
        name = "Ming"
        createdAt = "2009-01-14T04:33:35Z"
    }

    private fun viewState() = viewModel.viewState.value!!
}