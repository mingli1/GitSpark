package com.gitspark.gitspark.ui.main.tab.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.ui.base.BaseFragment
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*
import kotlinx.android.synthetic.main.profile_header.*

class OverviewFragment : BaseFragment<OverviewViewModel>(OverviewViewModel::class.java) {

    private var visible = false
    private var started = false

    private var imageUrl = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onStart() {
        super.onStart()
        started = true
        if (visible) viewModel.onResume()
    }

    override fun onStop() {
        super.onStop()
        started = false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        visible = isVisibleToUser
        if (visible && started) viewModel.onResume()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(this) { updateView(it) }
        viewModel.userDataMediator.observe(this) { viewModel.onCachedUserDataRetrieved(it) }
    }

    private fun updateView(viewState: OverviewViewState) {
        with (viewState) {
            if (imageUrl != avatarUrl && avatarUrl.isNotEmpty()) {
                avatar_image.loadImage(avatarUrl)
                imageUrl = avatarUrl
            }
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

            loading_indicator.isVisible = loading
        }
    }
}