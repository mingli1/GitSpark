package com.gitspark.gitspark.helper

import com.gitspark.gitspark.model.Repo
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val POPULAR_OR_PINNED_HEADER = "f4 mb-2 text-normal"
private const val REPO_FULL_NAME = "text-bold flex-auto min-width-0"
private const val REPO_DESC = "pinned-item-desc text-gray text-small"
private const val REPO_LANG = "itemprop=\"programmingLanguage\">"
private const val REPO_STARS = "stargazers\" class=\"pinned-item-meta muted-link"
private const val REPO_FORKS = "members\" class=\"pinned-item-meta muted-link"
private const val HREF = "href=\""
private const val QUOTE = "\""
private const val CLOSE = "</"

@Singleton
class PinnedReposHelper @Inject constructor() {

    fun parse(raw: String): Pair<String, List<Repo>> {
        val repos = mutableListOf<Repo>()
        val header = getPopularOrPinned(raw)
        if (raw.isEmpty()) return Pair(header, emptyList())

        val scanner = Scanner(raw)

        var repoFullName = ""
        var repoDesc = ""
        var repoLang = ""
        var repoStars = "0"
        var repoForks = "0"
        var count = 0

        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()

            when {
                line.contains(REPO_FULL_NAME) -> {
                    if (count > 0) {
                        repos.add(Repo(
                            fullName = repoFullName,
                            repoDescription = repoDesc,
                            repoLanguage = repoLang,
                            repoPushedAt = repoStars,
                            repoCreatedAt = repoForks
                        ))
                    }
                    repoDesc = ""
                    repoLang = ""
                    repoStars = "0"
                    repoForks = "0"

                    val start = line.indexOf(HREF) + HREF.length
                    val end = line.indexOf(QUOTE, start)
                    repoFullName = line.substring(start, end).substring(1)

                    count++
                }
                line.contains(REPO_DESC) -> repoDesc = scanner.nextLine().trim()
                line.contains(REPO_LANG) -> {
                    val start = line.indexOf(REPO_LANG) + REPO_LANG.length
                    val end = line.indexOf(CLOSE, start)
                    repoLang = line.substring(start, end)
                }
                line.contains(REPO_STARS) -> {
                    scanner.nextLine()
                    repoStars = scanner.nextLine().trim()
                }
                line.contains(REPO_FORKS) -> {
                    scanner.nextLine()
                    repoForks = scanner.nextLine().trim()
                }
            }
        }
        scanner.close()
        if (count > repos.size) {
            repos.add(Repo(
                fullName = repoFullName,
                repoDescription = repoDesc,
                repoLanguage = repoLang,
                repoPushedAt = repoStars,
                repoCreatedAt = repoForks
            ))
        }

        return Pair(header, repos)
    }

    private fun getPopularOrPinned(raw: String): String {
        var ret = ""
        val scanner = Scanner(raw)

        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            if (line.contains(POPULAR_OR_PINNED_HEADER)) {
                ret = scanner.nextLine().trim()
            }
        }

        scanner.close()
        return ret
    }
}