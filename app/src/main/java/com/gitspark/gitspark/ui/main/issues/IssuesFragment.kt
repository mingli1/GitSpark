package com.gitspark.gitspark.ui.main.issues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.gitspark.gitspark.R
import com.gitspark.gitspark.ui.main.MainActivity

class IssuesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_issues, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        arguments?.let {
            with (activity as MainActivity) {
                setSupportActionBar(toolbar)
                supportActionBar?.run {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
                    title = getString(R.string.issues_title)
                }
            }
        }

        return view
    }
}