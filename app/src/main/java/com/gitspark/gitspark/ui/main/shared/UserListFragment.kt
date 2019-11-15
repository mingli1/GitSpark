package com.gitspark.gitspark.ui.main.shared

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.gitspark.gitspark.R
import com.gitspark.gitspark.api.service.USER_PER_PAGE
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.PreferencesHelper
import com.gitspark.gitspark.model.User
import com.gitspark.gitspark.ui.adapter.UsersAdapter
import com.gitspark.gitspark.ui.main.profile.BUNDLE_USERNAME
import kotlinx.android.synthetic.main.fragment_list.*
import javax.inject.Inject

const val BUNDLE_TITLE = "BUNDLE_TITLE"
const val BUNDLE_USER_LIST_TYPE = "BUNDLE_USER_LIST_TYPE"
const val BUNDLE_ARGUMENTS = "BUNDLE_ARGUMENTS"

class UserListFragment : ListFragment<User, UserListViewModel>(UserListViewModel::class.java, USER_PER_PAGE) {

    @Inject lateinit var prefsHelper: PreferencesHelper
    private lateinit var usersAdapter: UsersAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        usersAdapter = UsersAdapter(viewModel, prefsHelper)
        if (item_list.adapter == null) item_list.adapter = usersAdapter

        val type = arguments?.getSerializable(BUNDLE_USER_LIST_TYPE) as UserListType? ?: UserListType.None
        val args = arguments?.getString(BUNDLE_ARGUMENTS) ?: ""
        viewModel.onStart(type, args)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.navigateToProfileAction.observe(viewLifecycleOwner) { navigateToProfileFragment(it) }
    }

    override fun updateView(viewState: ListViewState<User>) {
        super.updateView(viewState)
        with (viewState) {
            empty_text.text = getString(R.string.user_empty_text)
            if (updateAdapter) usersAdapter.setItems(list, isLastPage)
        }
    }

    private fun navigateToProfileFragment(username: String) {
        val data = Bundle().apply { putString(BUNDLE_USERNAME, username) }
        findNavController().navigate(R.id.action_user_list_fragment_to_profile_fragment, data)
    }
}