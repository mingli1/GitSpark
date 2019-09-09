# GitSpark [![Build Status](https://travis-ci.com/mingli1/GitSpark.svg?branch=master)](https://travis-ci.com/mingli1/GitSpark) [![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) 

An Android GitHub client made mostly to practice modern Android MVVM architecture.

## Install
To build locally, you'll need to get your own GitHub API keys by [creating an OAuth application](https://github.com/settings/applications/new) and in `GitSpark/gradle.properties` or `~/.gradle/gradle.properties` you need to add these lines
```
GITHUB_CLIENT_ID="your client id"
GITHUB_CLIENT_SECRET="your client secret"
```

## Libraries used
* [AndroidX](https://developer.android.com/jetpack/androidx)
* Android Jetpack
  * [Android KTX](https://developer.android.com/kotlin/ktx.html) for Kotlin
  * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata), [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) for MVVM architecture
  * [Navigation Component](https://developer.android.com/guide/navigation/)
  * [Room](https://developer.android.com/topic/libraries/architecture/room) for persistence
* [Dagger 2](https://github.com/google/dagger) for dependency injection
* [RxJava, RxKotlin, RxAndroid](https://github.com/ReactiveX/RxJava) for async calls
* [Retrofit 2](https://square.github.io/retrofit/) and [OkHttp3](https://square.github.io/okhttp/) for API calls
* [Moshi](https://github.com/square/moshi) for JSON parsing
* [Picasso](https://github.com/square/picasso) for images
* [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP) for time
* [JUnit 4](https://junit.org/junit4/) for unit tests
* [MockK](https://mockk.io/) for mocking in unit tests
* [AssertJ](https://joel-costigliola.github.io/assertj/) for better assertions in unit tests
* [MarkdownView](https://github.com/tiagohm/MarkdownView) for displaying markdown files
* [PullRefreshLayout](https://github.com/baoyongzhang/android-PullRefreshLayout) for nicer pull refresh animations