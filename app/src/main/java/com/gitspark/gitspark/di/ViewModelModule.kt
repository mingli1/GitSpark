package com.gitspark.gitspark.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gitspark.gitspark.ui.base.ViewModelFactory
import com.gitspark.gitspark.ui.dialog.AssigneesViewModel
import com.gitspark.gitspark.ui.dialog.LabelsViewModel
import com.gitspark.gitspark.ui.login.LoginViewModel
import com.gitspark.gitspark.ui.main.home.HomeViewModel
import com.gitspark.gitspark.ui.main.issues.IssueDetailViewModel
import com.gitspark.gitspark.ui.main.issues.IssueEditViewModel
import com.gitspark.gitspark.ui.main.issues.IssueEditSharedViewModel
import com.gitspark.gitspark.ui.main.profile.*
import com.gitspark.gitspark.ui.main.repo.RepoContentViewModel
import com.gitspark.gitspark.ui.main.repo.RepoDetailSharedViewModel
import com.gitspark.gitspark.ui.main.repo.RepoDetailViewModel
import com.gitspark.gitspark.ui.main.repo.RepoOverviewViewModel
import com.gitspark.gitspark.ui.main.search.SearchFilterViewModel
import com.gitspark.gitspark.ui.main.search.SearchSharedViewModel
import com.gitspark.gitspark.ui.main.search.SearchViewModel
import com.gitspark.gitspark.ui.main.settings.SettingsViewModel
import com.gitspark.gitspark.ui.main.shared.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(OverviewViewModel::class)
    internal abstract fun bindOverviewViewModel(overviewViewModel: OverviewViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ProfileFeedViewModel::class)
    internal abstract fun bindProfileFeedViewModel(profileFeedViewModel: ProfileFeedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ReposViewModel::class)
    internal abstract fun bindReposViewModel(reposViewModel: ReposViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(StarsViewModel::class)
    internal abstract fun bindStarsViewModel(starsViewModel: StarsViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    internal abstract fun bindEditProfileViewModel(editProfileViewModel: EditProfileViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(ProfileSharedViewModel::class)
    internal abstract fun bindProfileSharedViewModel(profileSharedViewModel: ProfileSharedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(RepoDetailViewModel::class)
    internal abstract fun bindRepoDetailViewModel(repoDetailViewModel: RepoDetailViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(RepoOverviewViewModel::class)
    internal abstract fun bindRepoOverviewViewModel(repoOverviewViewModel: RepoOverviewViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(RepoContentViewModel::class)
    internal abstract fun bindRepoContentViewModel(repoContentViewModel: RepoContentViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(RepoDetailSharedViewModel::class)
    internal abstract fun bindRepoDetailSharedViewModel(repoDetailSharedViewModel: RepoDetailSharedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(UserListViewModel::class)
    internal abstract fun bindUserListViewModel(userListViewModel: UserListViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(RepoListViewModel::class)
    internal abstract fun bindRepoListViewModel(repoListViewModel: RepoListViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(CommitListViewModel::class)
    internal abstract fun bindCommitListViewModel(commitListViewModel: CommitListViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(EventListViewModel::class)
    internal abstract fun bindEventListViewModel(eventListViewModel: EventListViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(UserSharedViewModel::class)
    internal abstract fun bindUserSharedViewModel(userSharedViewModel: UserSharedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(SearchFilterViewModel::class)
    internal abstract fun bindSearchFilterViewModel(searchFilterViewModel: SearchFilterViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(SearchSharedViewModel::class)
    internal abstract fun bindSearchSharedViewModel(searchSharedViewModel: SearchSharedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(IssuesListViewModel::class)
    internal abstract fun bindIssuesListViewModel(issuesListViewModel: IssuesListViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(IssueDetailViewModel::class)
    internal abstract fun bindIssueDetailViewModel(issueDetailViewModel: IssueDetailViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(IssueEditViewModel::class)
    internal abstract fun bindIssueEditViewModel(issueEditViewModel: IssueEditViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(AssigneesViewModel::class)
    internal abstract fun bindAssigneesViewModel(assigneesViewModel: AssigneesViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(LabelsViewModel::class)
    internal abstract fun bindLabelsViewModel(labelsViewModel: LabelsViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(IssueEditSharedViewModel::class)
    internal abstract fun bindIssueEditSharedViewModel(issueEditSharedViewModel: IssueEditSharedViewModel): ViewModel

    @Binds @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}