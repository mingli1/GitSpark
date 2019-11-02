package com.gitspark.gitspark.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.extension.observeOnce
import com.gitspark.gitspark.helper.LanguageColorHelper
import com.gitspark.gitspark.helper.TimeHelper
import com.gitspark.gitspark.model.Contribution
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.adapter.ReposAdapter
import com.gitspark.gitspark.ui.main.shared.BUNDLE_ARGUMENTS
import com.gitspark.gitspark.ui.main.shared.BUNDLE_TITLE
import com.gitspark.gitspark.ui.main.shared.BUNDLE_USER_LIST_TYPE
import com.gitspark.gitspark.ui.main.shared.UserListType
import com.gitspark.gitspark.ui.nav.BUNDLE_REPO_FULLNAME
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import kotlinx.android.synthetic.main.profile_header.*
import java.util.*
import javax.inject.Inject

const val BUNDLE_NAME = "BUNDLE_NAME"
const val BUNDLE_EMAIL = "BUNDLE_EMAIL"
const val BUNDLE_COMPANY = "BUNDLE_COMPANY"
const val BUNDLE_LOCATION = "BUNDLE_LOCATION"
const val BUNDLE_HIREABLE = "BUNDLE_HIREABLE"
const val BUNDLE_BIO = "BUNDLE_BIO"
const val BUNDLE_URL = "BUNDLE_URL"

class OverviewFragment : TabFragment<OverviewViewModel>(OverviewViewModel::class.java) {

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[ProfileSharedViewModel::class.java]
    }

    @Inject lateinit var colorHelper: LanguageColorHelper
    @Inject lateinit var timeHelper: TimeHelper
    private lateinit var reposAdapter: ReposAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reposAdapter = ReposAdapter(colorHelper, timeHelper, viewModel, simple = true)

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        pinned_repos.setHasFixedSize(true)
        pinned_repos.layoutManager = layoutManager
        if (pinned_repos.adapter == null) pinned_repos.adapter = reposAdapter

        setUpListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pinned_repos.adapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun viewModelOnResume() =
        viewModel.onResume(
            arguments?.getString(BUNDLE_USERNAME),
            (parentFragment as UserDataCallback).getData()
        )

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.userDataMediator.observeOnce(viewLifecycleOwner) { viewModel.onCachedUserDataRetrieved(it) }
        viewModel.contributionsAction.observe(viewLifecycleOwner) { updateContributionsView(it) }
        viewModel.navigateToFollowersAction.observe(viewLifecycleOwner) { navigateToUserListFragment(it) }
        viewModel.navigateToFollowingAction.observe(viewLifecycleOwner) { navigateToUserListFragment(it) }
        viewModel.refreshAction.observe(viewLifecycleOwner) {
            (parentFragment as UserDataCallback).refreshUserData(arguments?.getString(
                BUNDLE_USERNAME
            )!!)
        }
        viewModel.navigateToEditProfileAction.observe(viewLifecycleOwner) { navigateToEditProfileFragment(it) }
        sharedViewModel.userData.observe(viewLifecycleOwner) { viewModel.onUserDataRefreshed(it) }
        viewModel.navigateToRepoDetailAction.observe(viewLifecycleOwner) { navigateToRepoDetailFragment(it) }
    }

    fun notifyUserDataRefreshed() =
        viewModel.onUserDataRefreshed((parentFragment as UserDataCallback).getData()!!)

    private fun updateView(viewState: OverviewViewState) {
        with (viewState) {
            if (avatarUrl.isNotEmpty()) avatar_image.loadImage(avatarUrl)
            if (nameText.isEmpty()) {
                name_field.text = usernameText
                username_field.isVisible = false
            }
            else {
                name_field.text = nameText
                username_field.text = usernameText
            }
            plan_name_field.isVisible = planName.isNotEmpty() && planName != "free"
            plan_name_field.text = planName
            bio_field.isVisible = bioText.isNotEmpty()
            bio_field.text = bioText

            edit_profile_button.isVisible = authUser
            follows_button.isVisible = !authUser
            follows_button.text =
                if (isFollowing) getString(R.string.unfollow_button_text)
                else getString(R.string.follow_button_text)
            is_following_text.isVisible = !authUser && isFollowing

            location_field.isVisible = locationText.isNotEmpty()
            location_field.text = locationText
            email_field.isVisible = emailText.isNotEmpty()
            email_field.text = emailText
            url_field.isVisible = urlText.isNotEmpty()
            url_field.text = urlText
            company_field.isVisible = companyText.isNotEmpty()
            company_field.text = companyText

            following_field.text = getString(R.string.following_text, numFollowing)
            followers_field.text = getString(R.string.followers_text, numFollowers)

            total_contributions_field.text =
                if (totalContributions == 1) getString(R.string.total_contribution_single)
                else getString(R.string.total_contribution, totalContributions)
            created_at_field.text = getString(R.string.joined_date, createdDate)

            loading_indicator.isVisible = loading
            swipe_refresh.setRefreshing(refreshing)

            reposAdapter.setItems(pinnedRepos, true)
            pinned_button.setImageResource(if (pinnedReposShown)
                R.drawable.ic_expand_less else R.drawable.ic_expand_more)
            pinned_repos.isVisible = pinnedReposShown
            pinned_field.text = if (pinnedReposHeader.isEmpty()) getString(R.string.pinned_repositories_default)
                else getString(R.string.num_repos_text, pinnedReposHeader, pinnedRepos.size)
        }
    }

    private fun updateContributionsView(data: SortedMap<String, List<Contribution>>) {
        contributions_view.createBitmap(data)
    }

    private fun setUpListeners() {
        swipe_refresh.setOnRefreshListener { viewModel.onRefresh() }
        followers_field.setOnClickListener { viewModel.onFollowersFieldClicked() }
        following_field.setOnClickListener { viewModel.onFollowingFieldClicked() }
        follows_button.setOnClickListener {
            viewModel.onFollowsButtonClicked(follows_button.text == getString(R.string.unfollow_button_text))
        }
        edit_profile_button.setOnClickListener { viewModel.onEditProfileButtonClicked() }
        pinned_button.setOnClickListener { viewModel.onPinnedReposButtonClicked() }
    }

    private fun navigateToEditProfileFragment(user: User) {
        findNavController().navigate(
            R.id.action_profile_fragment_to_edit_profile_fragment,
            getEditProfileBundle(user)
        )
    }

    private fun navigateToUserListFragment(triple: Triple<UserListType, String, String>) {
        val data = Bundle().apply {
            putSerializable(BUNDLE_USER_LIST_TYPE, triple.first)
            putString(BUNDLE_TITLE, triple.second)
            putString(BUNDLE_ARGUMENTS, triple.third)
        }
        findNavController().navigate(
            R.id.action_profile_fragment_to_user_list_fragment,
            data
        )
    }

    private fun navigateToRepoDetailFragment(args: String) {
        val data = Bundle().apply { putString(BUNDLE_REPO_FULLNAME, args) }
        findNavController().navigate(
            R.id.action_profile_fragment_to_repo_detail_fragment,
            data
        )
    }

    companion object {
        fun getEditProfileBundle(user: User) = Bundle().apply {
            putString(BUNDLE_NAME, user.name)
            putString(BUNDLE_EMAIL, user.email)
            putString(BUNDLE_COMPANY, user.company)
            putString(BUNDLE_LOCATION, user.location)
            putBoolean(BUNDLE_HIREABLE, user.hireable)
            putString(BUNDLE_BIO, user.bio)
            putString(BUNDLE_URL, user.blogUrl)
        }
    }
}