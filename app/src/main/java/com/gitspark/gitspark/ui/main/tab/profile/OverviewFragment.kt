package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.observeOnce
import com.gitspark.gitspark.model.Contribution
import com.gitspark.gitspark.ui.main.tab.BUNDLE_USERNAME
import com.gitspark.gitspark.ui.main.tab.ProfileFragment
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import kotlinx.android.synthetic.main.profile_header.*
import java.util.*

class OverviewFragment : TabFragment<OverviewViewModel>(OverviewViewModel::class.java) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpListeners()
    }

    override fun viewModelOnResume() = viewModel.onResume(arguments?.getString(BUNDLE_USERNAME))

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.userDataMediator.observeOnce(viewLifecycleOwner) { viewModel.onCachedUserDataRetrieved(it) }
        viewModel.contributionsAction.observe(viewLifecycleOwner) { updateContributionsView(it) }
        viewModel.navigateToFollowsAction.observe(viewLifecycleOwner) { navigateToFollowsFragment(it) }
    }

    private fun updateView(viewState: OverviewViewState) {
        with (viewState) {
            if (avatarUrl.isNotEmpty()) avatar_image.loadImage(avatarUrl)
            name_field.text = nameText
            username_field.text = usernameText
            plan_name_field.isVisible = planName != "free"
            plan_name_field.text = planName
            bio_field.isVisible = bioText.isNotEmpty()
            bio_field.text = bioText

            location_field.isVisible = locationText.isNotEmpty()
            location_field.text = locationText
            email_field.isVisible = emailText.isNotEmpty()
            email_field.text = emailText
            company_field.isVisible = companyText.isNotEmpty()
            company_field.text = companyText

            following_field.text = getString(R.string.following_text, numFollowing)
            followers_field.text = getString(R.string.followers_text, numFollowers)

            total_contributions_field.text = getString(R.string.total_contribution, totalContributions)
            created_at_field.text = getString(R.string.joined_date, createdDate)

            loading_indicator.isVisible = loading
            swipe_refresh.setRefreshing(refreshing)
        }
    }

    private fun updateContributionsView(data: SortedMap<String, List<Contribution>>) {
        contributions_view.createBitmap(data)
    }

    private fun setUpListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        followers_field.setOnClickListener { viewModel.onFollowsFieldClicked(FollowState.Followers) }
        following_field.setOnClickListener { viewModel.onFollowsFieldClicked(FollowState.Following) }
    }

    private fun navigateToFollowsFragment(followState: FollowState) {
        (parentFragment as ProfileFragment).navigateToFollowsFragment(followState)
    }
}