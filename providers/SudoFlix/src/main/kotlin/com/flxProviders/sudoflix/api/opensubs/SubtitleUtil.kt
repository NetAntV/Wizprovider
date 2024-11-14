package com.flxProviders.sudoflix.api.opensubs

import com.flixclusive.core.util.exception.safeCall
import com.flixclusive.core.util.network.json.fromJson
import com.flixclusive.core.util.network.okhttp.request
import com.flixclusive.model.provider.link.Subtitle
import com.flixclusive.model.provider.link.SubtitleSource
import okhttp3.OkHttpClient

internal object SubtitleUtil {
    private const val WIZDOM_API_ENDPOINT = "https://wizdom.xyz/api"
    private const val OPEN_SUBS_STREMIO_ENDPOINT = "https://opensubtitles-v3.strem.io"

    // DTO for Wizdom API response
    private data class SubtitleItem(
        val id: Int,
        val versioname: String,
        val score: Int
    )

    // DTO for OpenSubtitles Stremio API response
    private data class OpenSubtitleStremioDto(
        val subtitles: List<Map<String, String>>,
    )

    fun OkHttpClient.fetchSubtitles(
        imdbId: String,
        season: Int? = null,
        episode: Int? = null,
        onSubtitleLoaded: (Subtitle) -> Unit
    ) {
        // Fetch Wizdom subtitles first, then fetch OpenSubtitles
        fetchWizdomSubtitles(imdbId, season, episode) {
            onSubtitleLoaded(it)
            // After loading Wizdom subtitles, start fetching OpenSubtitles
            fetchOpenSubsSubtitles(imdbId, season, episode, onSubtitleLoaded)
        }
    }

    private fun OkHttpClient.fetchWizdomSubtitles(
        imdbId: String,
        season: Int? = null,
        episode: Int? = null,
        onSubtitleLoaded: (Subtitle) -> Unit
    ) {
        val slug = if (season == null) {
            "by_id&imdb=$imdbId"
        } else {
            "by_id&imdb=$imdbId&season=$season&episode=$episode"
        }
        val url = "$WIZDOM_API_ENDPOINT/search?action=$slug"
        println("Fetching Wizdom subtitles from URL: $url")

        val subtitles = request(url)
            .execute()
            .use {
                val string = it.body?.string()

                if (!it.isSuccessful || string == null) {
                    println("Wizdom API request failed or returned null response")
                    return
                }

                println("Wizdom API Raw JSON response: $string")

                safeCall {
                    fromJson<List<SubtitleItem>>(string)
                }
            }

        println("Parsed Wizdom subtitles: $subtitles")

        subtitles?.forEach { subtitle ->
            val subLanguage = "heb"  // Hard-coded language for Wizdom API
            val subtitleUrl = "https://wizdom.xyz/api/files/sub/${subtitle.id}"
            println("Wizdom Subtitle ID: ${subtitle.id}, Generated URL: $subtitleUrl, Version: ${subtitle.versioname}")

            val subtitleDto = Subtitle(
                url = subtitleUrl,
                language = "[Wizdom] $subLanguage",
                type = SubtitleSource.ONLINE
            )

            println("Loading Wizdom Subtitle: $subtitleDto")
            onSubtitleLoaded(subtitleDto)
        }
    }

    private fun OkHttpClient.fetchOpenSubsSubtitles(
        imdbId: String,
        season: Int? = null,
        episode: Int? = null,
        onSubtitleLoaded: (Subtitle) -> Unit
    ) {
        val slug = if (season == null) {
            "movie/$imdbId"
        } else {
            "series/$imdbId:$season:$episode"
        }
        val url = "$OPEN_SUBS_STREMIO_ENDPOINT/subtitles/$slug.json"
        println("Fetching OpenSubtitles Stremio subtitles from URL: $url")

        val subtitles = request(url)
            .execute()
            .use {
                val string = it.body?.string()

                if (!it.isSuccessful || string == null) {
                    println("OpenSubtitles API request failed or returned null response")
                    return
                }

                println("OpenSubtitles API Raw JSON response: $string")

                safeCall {
                    fromJson<OpenSubtitleStremioDto>(string)
                }
            }

        println("Parsed OpenSubtitles Stremio subtitles: ${subtitles?.subtitles}")

        subtitles?.subtitles?.forEach { subtitle ->
            val subLanguage = subtitle["lang"] ?: return@forEach
            if (subLanguage == "eng" || subLanguage == "heb") {
                val subtitleUrl = subtitle["url"] ?: return@forEach
                println("OpenSubtitles English Subtitle URL: $subtitleUrl")

                val subtitleDto = Subtitle(
                    url = subtitleUrl,
                    language = "[OpenSubs] $subLanguage",
                    type = SubtitleSource.ONLINE
                )

                println("Loading OpenSubtitles English Subtitle: $subtitleDto")
                onSubtitleLoaded(subtitleDto)
            }
        }
    }
}
