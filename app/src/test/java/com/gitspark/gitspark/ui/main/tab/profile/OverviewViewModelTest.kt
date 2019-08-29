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
    fun shouldGetUserDataOnResume() {
        viewModel.onResume(username = "username")
        assertThat(viewState()).isEqualTo(OverviewViewState(loading = true))
        verify { userRepository.getUser("username") }
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
    fun shouldRequestUserWhenUsernameNotNullOnRefresh() {
        viewModel.onResume(username = "username")
        viewModel.onRefresh()
        verify { userRepository.getUser("username") }
    }

    @Test
    fun shouldGetUserDataOnSuccess() {
        every { userRepository.getUser(any()) } returns
                Observable.just(UserResult.Success(getUser()))

        viewModel.onResume(username = "username")

        assertThat(viewState().nameText).isEqualTo("Steven")
        assertThat(viewState().usernameText).isEqualTo("orz39")
        assertThat(viewState().avatarUrl).isEqualTo("avatarUrl")
        assertThat(viewState().bioText).isEqualTo("bio")
        assertThat(viewState().locationText).isEqualTo("NJ")
        assertThat(viewState().emailText).isEqualTo("orz39@gmail.com")
        assertThat(viewState().companyText).isEqualTo("Google")
        assertThat(viewState().numFollowers).isEqualTo(999)
        assertThat(viewState().numFollowing).isEqualTo(999)
        assertThat(viewState().planName).isEqualTo("")
        assertThat(viewState().createdDate).isEqualTo("01-14-2008 04:33:35")
    }

    @Test
    fun shouldGetUserDataFailure() {
        every { userRepository.getUser(any()) } returns
                Observable.just(UserResult.Failure("failure"))

        viewModel.onResume(username = "username")

        assertThat(viewModel.alertAction.value).isEqualTo("failure")
        assertThat(viewState().loading).isFalse()
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