package com.nl.customnaverblog

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

val LocalChromeCustomTabs = staticCompositionLocalOf<IChromeCustomTabs> {
    noLocalProvidedFor("LocalChromeCustomTab")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

@Composable
fun ProvideChromeCustomTabs(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalChromeCustomTabs provides ChromeCustomTab(LocalContext.current, true),
        content = content
    )
}

interface IChromeCustomTabs {
    fun launch(url: String)
    fun launch(uri: Uri)
}

class ChromeCustomTab(
    private val context: Context,
    private val useIncognito: Boolean = false
): IChromeCustomTabs {

    private val customTabs: CustomTabsIntent
        get() {
            return CustomTabsIntent.Builder()
                .setToolbarCornerRadiusDp(8)
                .setTranslateLocale(Locale.getDefault())
                .setShowTitle(true)
                .setBookmarksButtonEnabled(true)
                .setBackgroundInteractionEnabled(true)
                .setDownloadButtonEnabled(true)
                .setInstantAppsEnabled(true)
                .setUrlBarHidingEnabled(true)
                .setShareState(CustomTabsIntent.SHARE_STATE_ON)
                .build()
        }

    override fun launch(url: String) {
        launch(Uri.parse(url))
    }

    override fun launch(uri: Uri) {
        customTabs.apply {
            @Suppress("SimplifyBooleanWithConstants")
            if (useIncognito == true) {
                intent.putExtra(
                    "com.google.android.apps.chrome.EXTRA_OPEN_NEW_INCOGNITO_TAB",
                    true
                )
            }
        }.launchUrl(context, uri)
    }

}