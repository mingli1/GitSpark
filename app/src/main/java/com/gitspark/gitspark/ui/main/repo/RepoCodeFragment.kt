package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.adapter.BUNDLE_FILE_CONTENT
import com.gitspark.gitspark.ui.adapter.BUNDLE_FILE_EXTENSION
import kotlinx.android.synthetic.main.fragment_repo_code.*

class RepoCodeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_repo_code, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val content = arguments?.getString(BUNDLE_FILE_CONTENT) ?: ""
        val extension = arguments?.getString(BUNDLE_FILE_EXTENSION) ?: ""
        code_view.setCode(content, extension)
    }
}