package com.flixclusive.provider.basic_dummy_webview_provider

import android.content.Context
import androidx.compose.runtime.Composable
import com.flixclusive.provider.FlixclusiveProvider
import com.flixclusive.provider.Provider
import com.flixclusive.provider.ProviderApi
import com.flixclusive.provider.basic_dummy_webview_provider.BasicDummyWebViewProviderApi
import com.flixclusive.provider.basic_dummy_webview_provider.ExampleSettingsScreen
import okhttp3.OkHttpClient

/**
 * ## The main class for a Flixclusive provider.
 *
 * This one uses a WebView.
 *
 * A [Provider] acts as a middleman between your [Provider]
 * and the application. It facilitates the lifecycle of your [Provider],
 * ensuring seamless integration with the application.
 *
 * #### WARN: Every provider must be annotated with [FlixclusiveProvider].
 *
 * To create a provider, extend this class and override the necessary methods.
 *
 * @see [TODO: ADD DOCS LINK FOR PLUGIN CREATION !!!!!]
 */
@FlixclusiveProvider
class BasicDummyWebViewProvider : Provider() {
    @Composable
    override fun SettingsScreen() {
        // Create a custom component for code readability
        ExampleSettingsScreen(resources = resources)
    }

    override fun getApi(
        context: Context,
        client: OkHttpClient
    ): ProviderApi {
        return BasicDummyWebViewProviderApi(
            client = client,
            context = context,
            provider = this
        )
    }

    override fun onUnload(context: Context?) {
        super.onUnload(context)
    }
}
