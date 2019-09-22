package com.gitspark.gitspark.ui.adapter

const val BUNDLE_FILE_CONTENT = "BUNDLE_FILE_CONTENT"

interface RepoContentNavigator {
    fun onDirectorySelected(path: String)
    fun onFileSelected(url: String)
}