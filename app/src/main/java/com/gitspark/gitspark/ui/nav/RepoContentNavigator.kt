package com.gitspark.gitspark.ui.nav

const val BUNDLE_FILE_CONTENT = "BUNDLE_FILE_CONTENT"
const val BUNDLE_FILE_NAME = "BUNDLE_FILE_NAME"
const val BUNDLE_FILE_EXTENSION = "BUNDLE_FILE_EXTENSION"

interface RepoContentNavigator {

    fun onDirectorySelected(path: String)

    fun onFileSelected(url: String, fileName: String, extension: String)
}