package com.gitspark.gitspark.ui.main.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.gitspark.gitspark.R
import com.gitspark.gitspark.databinding.FragmentSettingsBinding
import com.gitspark.gitspark.extension.observe
import com.gitspark.gitspark.helper.DarkModeConfig
import com.gitspark.gitspark.helper.DarkModeHelper
import com.gitspark.gitspark.ui.base.BaseFragment
import com.gitspark.gitspark.ui.dialog.SelectDialog
import com.gitspark.gitspark.ui.dialog.SelectDialogCallback
import com.gitspark.gitspark.ui.main.MainActivity

class SettingsFragment : BaseFragment<SettingsViewModel>(SettingsViewModel::class.java), SelectDialogCallback {

    private lateinit var darkModeHelper: DarkModeHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        val view = binding.root

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        }

        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.viewModel = viewModel

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        darkModeHelper = DarkModeHelper(context!!)
    }

    override fun onSelected(item: String) {
        viewModel.onThemeSelected(item)
        darkModeHelper.setDarkMode(DarkModeConfig.valueOf(item))
    }

    override fun observeViewModel() {
        viewModel.showThemeSelector.observe(viewLifecycleOwner) {
            SelectDialog.newInstance(
                "Select Theme",
                arrayOf("Light", "Dark", "System"),
                "Light",
                "Set Theme"
            ).show(childFragmentManager, null)
        }
    }
}