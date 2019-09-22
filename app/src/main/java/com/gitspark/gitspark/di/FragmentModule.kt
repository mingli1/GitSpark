package com.gitspark.gitspark.di

import com.gitspark.gitspark.ui.main.feed.FeedFragment
import com.gitspark.gitspark.ui.main.issues.IssuesFragment
import com.gitspark.gitspark.ui.main.pr.PullRequestsFragment
import com.gitspark.gitspark.ui.main.profile.*
import com.gitspark.gitspark.ui.main.repo.RepoContentFragment
import com.gitspark.gitspark.ui.main.repo.RepoDetailFragment
import com.gitspark.gitspark.ui.main.repo.RepoOverviewFragment
import com.gitspark.gitspark.ui.main.search.SearchFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentModule {

    @ContributesAndroidInjector
    internal abstract fun contributesFeedFragment(): FeedFragment

    @ContributesAndroidInjector
    internal abstract fun contributesProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    internal abstract fun contributesSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    internal abstract fun contributesPullRequestsFragment(): PullRequestsFragment

    @ContributesAndroidInjector
    internal abstract fun contributesIssuesFragment(): IssuesFragment

    @ContributesAndroidInjector
    internal abstract fun contributesOverviewFragment(): OverviewFragment

    @ContributesAndroidInjector
    internal abstract fun contributesReposFragment(): ReposFragment

    @ContributesAndroidInjector
    internal abstract fun contributesFollowsFragment(): FollowsFragment

    @ContributesAndroidInjector
    internal abstract fun contributesStarsFragment(): StarsFragment

    @ContributesAndroidInjector
    internal abstract fun contributesEditProfileFragment(): EditProfileFragment

    @ContributesAndroidInjector
    internal abstract fun contributesRepoDetailFragment(): RepoDetailFragment

    @ContributesAndroidInjector
    internal abstract fun contributesRepoOverviewFragment(): RepoOverviewFragment

    @ContributesAndroidInjector
    internal abstract fun contributesRepoContentFragment(): RepoContentFragment
}