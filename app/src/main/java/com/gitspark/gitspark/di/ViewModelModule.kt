package com.gitspark.gitspark.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gitspark.gitspark.ui.base.ViewModelFactory
import com.gitspark.gitspark.ui.login.LoginViewModel
import com.gitspark.gitspark.ui.main.tab.*
import com.gitspark.gitspark.ui.main.tab.profile.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(FeedViewModel::class)
    internal abstract fun bindFeedViewModel(feedViewModel: FeedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    internal abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(PullRequestsViewModel::class)
    internal abstract fun bindPullRequestsViewModel(pullRequestsViewModel: PullRequestsViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(IssuesViewModel::class)
    internal abstract fun bindIssuesViewModel(issuesViewModel: IssuesViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(OverviewViewModel::class)
    internal abstract fun bindOverviewViewModel(overviewViewModel: OverviewViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ReposViewModel::class)
    internal abstract fun bindReposViewModel(reposViewModel: ReposViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(FollowsViewModel::class)
    internal abstract fun bindFollowsViewModel(followsViewModel: FollowsViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(StarsViewModel::class)
    internal abstract fun bindStarsViewModel(starsViewModel: StarsViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    internal abstract fun bindEditProfileViewModel(editProfileViewModel: EditProfileViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ProfileSharedViewModel::class)
    internal abstract fun bindProfileSharedViewModel(profileSharedViewModel: ProfileSharedViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}