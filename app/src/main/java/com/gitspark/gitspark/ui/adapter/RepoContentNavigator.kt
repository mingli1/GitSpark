package com.gitspark.gitspark.ui.adapter

const val BUNDLE_FILE_CONTENT = "BUNDLE_FILE_CONTENT"
const val BUNDLE_FILE_NAME = "BUNDLE_FILE_NAME"

interface RepoContentNavigator {
    fun onDirectorySelected(path: String)
    fun onFileSelected(url: String, fileName: String)
}