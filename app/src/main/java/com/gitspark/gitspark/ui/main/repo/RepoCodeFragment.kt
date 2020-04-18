package com.gitspark.gitspark.ui.main.repo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import br.tiagohm.markdownview.css.styles.Github
import com.gitspark.gitspark.R
import com.gitspark.gitspark.extension.isVisible
import com.gitspark.gitspark.extension.loadImage
import com.gitspark.gitspark.helper.DarkModeHelper
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_CONTENT
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_EXTENSION
import com.gitspark.gitspark.ui.nav.BUNDLE_FILE_NAME
import com.gitspark.gitspark.ui.main.MainActivity
import dagger.android.support.AndroidSupportInjection
import io.github.kbiakov.codeview.highlight.ColorTheme
import kotlinx.android.synthetic.main.fragment_repo_code.*
import javax.inject.Inject

class RepoCodeFragment : Fragment() {

    @Inject lateinit var darkModeHelper: DarkModeHelper

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

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
                with (code_view) {
                    isVisible = true
                    setCode(content)
                    getOptions()?.setTheme(if (darkModeHelper.isDarkMode()) ColorTheme.MONOKAI else ColorTheme.DEFAULT)
                }
            }
        }
    }
}