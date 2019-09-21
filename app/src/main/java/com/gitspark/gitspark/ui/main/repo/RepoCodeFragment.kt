package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.gitspark.gitspark.R
import com.gitspark.gitspark.model.Branch
import com.gitspark.gitspark.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_repo_code.*

class RepoCodeFragment : BaseFragment<RepoCodeViewModel>(RepoCodeViewModel::class.java) {

    private lateinit var branchSpinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_code, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun notifyBranchDataRetrieved(branches: List<Branch>) {
        val branchNames = branches.map { it.name }
        branchSpinnerAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item,
            branchNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        branch_spinner.adapter = branchSpinnerAdapter
    }

    override fun observeViewModel() {

    }
}