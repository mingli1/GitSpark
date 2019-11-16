package com.gitspark.gitspark.ui.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.model.ApiEditProfileRequest
import com.gitspark.gitspark.extension.afterTextChanged
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.model.AuthUser
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.full_screen_progress_spinner.*

class EditProfileFragment : BaseFragment<EditProfileViewModel>(EditProfileViewModel::class.java) {

    private val sharedViewModel by lazy {
        ViewModelProviders.of(activity!!, viewModelFactory)[ProfileSharedViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            viewModel.fillInitialData(ApiEditProfileRequest(
                name = it.getString(BUNDLE_NAME) ?: "",
                bio = it.getString(BUNDLE_BIO) ?: "",
                email = it.getString(BUNDLE_EMAIL) ?: "",
                company = it.getString(BUNDLE_COMPANY) ?: "",
                location = it.getString(BUNDLE_LOCATION) ?: "",
                url = it.getString(BUNDLE_URL) ?: "",
                hireable = it.getBoolean(BUNDLE_HIREABLE)
            ))
        }

        setUpListeners()
    }

    override fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { updateView(it) }
        viewModel.finishFragmentAction.observe(viewLifecycleOwner) { finishFragment(it) }
    }

    private fun updateView(viewState: EditProfileViewState) {
        with (viewState) {
            loading_indicator.isVisible = loading

            if (edit_name.text.toString() != nameText) edit_name.setText(nameText)
            if (edit_bio.text.toString() != bioText) edit_bio.setText(bioText)
            if (edit_email.text.toString() != emailText) edit_email.setText(emailText)
            if (edit_url.text.toString() != urlText) edit_url.setText(urlText)
            if (edit_company.text.toString() != companyText) edit_company.setText(companyText)
            if (edit_location.text.toString() != locationText) edit_location.setText(locationText)
            if (hireable_checkbox.isChecked != hireable) hireable_checkbox.isChecked = hireable

            update_profile_button.isEnabled =
                arguments?.getString(BUNDLE_NAME) != nameText ||
                        arguments?.getString(BUNDLE_BIO) != bioText ||
                        arguments?.getString(BUNDLE_EMAIL) != emailText ||
                        arguments?.getString(BUNDLE_COMPANY) != companyText ||
                        arguments?.getString(BUNDLE_LOCATION) != locationText ||
                        arguments?.getString(BUNDLE_URL) != urlText ||
                        arguments?.getBoolean(BUNDLE_HIREABLE) != hireable

        }
    }

    private fun setUpListeners() {
        with (viewModel) {
            edit_name.afterTextChanged { onNameEdited(edit_name.text.toString()) }
            edit_bio.afterTextChanged { onBioEdited(edit_bio.text.toString()) }
            edit_email.afterTextChanged { onEmailEdited(edit_email.text.toString()) }
            edit_url.afterTextChanged { onUrlEdited(edit_url.text.toString()) }
            edit_company.afterTextChanged { onCompanyEdited(edit_company.text.toString()) }
            edit_location.afterTextChanged { onLocationEdited(edit_location.text.toString()) }
            hireable_checkbox.setOnCheckedChangeListener { _, isChecked -> onHireabledChecked(isChecked) }
            update_profile_button.setOnClickListener { onUpdateProfileClicked() }
        }
    }

    private fun finishFragment(user: AuthUser) {
        sharedViewModel.userData.value = user
        findNavController().navigateUp()
    }
}