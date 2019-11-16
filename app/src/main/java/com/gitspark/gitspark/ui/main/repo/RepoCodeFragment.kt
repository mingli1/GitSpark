package com.gitspark.gitspark.ui.main.repo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.tiagohm.codeview.Language
import br.tiagohm.codeview.Theme
import br.tiagohm.markdownview.css.styles.Github
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_CONTENT
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_EXTENSION
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_NAME
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
                setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                title = arguments?.getString(BUNDLE_FILE_NAME) ?: "File"
            }
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        markdown_view.addStyleSheet(Github())

        val content = arguments?.getString(BUNDLE_FILE_CONTENT) ?: ""

        when (arguments?.getString(BUNDLE_FILE_EXTENSION) ?: "") {
            "png", "jpg", "jpeg", "gif", "bmp" -> {
                image_view.isVisible = true
                image_view.loadImage(content)
            }
            "md" -> {
                markdown_view.isVisible = true
                markdown_view.loadMarkdownFromUrl(content)
            }
            else -> {
                code_view.isVisible = true
                code_view.run {
                    theme = Theme.GITHUB
                    code = content
                    language = Language.AUTO
                    apply()
                }
            }
        }
    }
}