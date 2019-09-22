package com.gitspark.gitspark.ui.adapter

const val BUNDLE_FILE_CONTENT = "BUNDLE_FILE_CONTENT"
const val BUNDLE_FILE_EXTENSION = "BUNDLE_FILE_EXTENSION"

interface RepoContentNavigator {
    fun onDirectorySelected(path: String)
    fun onFileSelected(url: String, extension: String)
}