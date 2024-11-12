import com.flixclusive.model.provider.Language
import com.flixclusive.model.provider.ProviderType
import com.flixclusive.model.provider.Status

dependencies {
    /*
     * Custom dependencies for each provider should be implemented here.
     * */
    // implementation( ... )

    // Comment if not implementing own SettingsScreen
    // No need to specify compose version explicitly
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.runtime:runtime")
    // ================= END: COMPOSE UI =================

}

android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

flxProvider {
    // ====== Provider Information =======
    adult.set(false)
    // providerName.set("Test WebView Provider") <-- You can customize the name by uncommenting this one.
    description.set("A dummy provider (with WebView) that does nothing.")

    versionMajor = 1
    versionMinor = 0
    versionPatch = 0
    versionBuild = 0

    // Changelog of your plugin
    changelog.set(
        """
        TODO: Add your changes here...
        """.trimIndent()
    ) // OPTIONAL

    /**
     * Add additional authors to this plugin
     * */
    author("FirstAuthor")
    author("SecondAuthor")
    // author( ... )

    /**
     * If your provider has an icon, put its image url here.
     * */
    // iconUrl.set("https://cool.png") // OPTIONAL

    /**
     * The main language of your provider.
     *
     * There are two supported values:
     * - Language.Multiple
     *      > Obviously for providers w/ multiple language support.
     * - Language("en")
     *      > For specific languages only. NOTE: Use the language's short-hand code.
     */
    language.set(Language.Multiple)

    /**
     * The main type that your provider supports.
     *
     * These are the possible values you could set:
     * - ProviderType.All
     * - ProviderType.TvShows
     * - ProviderType.Movies
     * - ProviderType(customType: String) // i.e., ProviderType("Anime")
     */
    providerType.set(ProviderType.All)

    /**
     * The current status of this provider.
     *
     * These are the possible values you could set:
     * - Status.Beta
     * - Status.Maintenance
     * - Status.Down
     * - Status.Working
     */
    status.set(Status.Working)
    // ================


    // === Utilities ===
    /**
     * Toggle this if this provider has its own resources.
     */
    requiresResources.set(true)

    /**
     * Excludes this plugin from the updater,
     * meaning it won't show up for users.
     * Set this if the plugin is unfinished
     */
    // excludeFromUpdaterJson.set(true) // OPTIONAL
    // =================
}

