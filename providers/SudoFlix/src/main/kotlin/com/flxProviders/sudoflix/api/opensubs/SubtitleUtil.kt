package com.flxProviders.sudoflix.api.opensubs

import com.flixclusive.core.util.exception.safeCall
import com.flixclusive.core.util.network.json.fromJson
import com.flixclusive.core.util.network.okhttp.request
import com.flixclusive.model.provider.link.Subtitle
import com.flixclusive.model.provider.link.SubtitleSource
import okhttp3.OkHttpClient

internal object SubtitleUtil {
    private const val OPEN_SUBS_STREMIO_ENDPOINT = "https://wizdom.xyz/api"

    // Update the DTO to directly match each subtitle item in the array
    private data class SubtitleItem(
        val id: Int,
        val versioname: String,
        val score: Int
    )

    fun OkHttpClient.fetchSubtitles(
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

        // Debug print for constructed URL
        val url = "$OPEN_SUBS_STREMIO_ENDPOINT/search?action=$slug"
        //println("Fetching subtitles from URL: $url")

        // Modify to parse the JSON array directly
        val subtitles = request(url)
            .execute()
            .use {
                val string = it.body?.string()

                if (!it.isSuccessful || string == null) {
                    println("Request failed or returned null response")
                    return
                }

                // Debug print for raw JSON response
                //println("Raw JSON response: $string")

                // Parse as a list of SubtitleItem
                safeCall {
                    fromJson<List<SubtitleItem>>(string)
                }
            }

        // Debug print for parsed subtitle list
        //println("Parsed subtitles: $subtitles")

        subtitles?.forEach { subtitle ->
            // Set the language to "heb" as per the requirement
            val subLanguage = "eng"
            // Generate the URL based on the subtitle ID
            val subtitleUrl = "https://wizdom.xyz/api/files/sub/${subtitle.id}"

            // Debug print for each subtitle item before creating Subtitle object
            //println("Subtitle ID: ${subtitle.id}, Generated URL: $subtitleUrl, Version: ${subtitle.versioname}")

            val subtitleDto = Subtitle(
                url = subtitleUrl,
                language = "[Wizdom] $subLanguage",
                type = SubtitleSource.ONLINE
            )

            // Debug print for Subtitle DTO being loaded
            //println("Loading Subtitle: $subtitleDto")
            onSubtitleLoaded(subtitleDto)
        }
    }
}
