package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.tiagohm.codeview.Language
import br.tiagohm.codeview.Theme
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.adapter.BUNDLE_FILE_CONTENT
import com.gitspark.gitspark.ui.adapter.BUNDLE_FILE_NAME
import com.gitspark.gitspark.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_repo_code.*

class RepoCodeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_repo_code, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        with (activity as MainActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.run {
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                title = arguments?.getString(BUNDLE_FILE_NAME) ?: "File"
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val content = arguments?.getString(BUNDLE_FILE_CONTENT) ?: ""

        code_view.run {
            theme = Theme.GITHUB
            code = content
            language = Language.AUTO
            apply()
        }
    }
}