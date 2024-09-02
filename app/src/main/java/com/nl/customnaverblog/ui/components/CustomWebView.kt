package com.nl.customnaverblog.ui.components

import android.content.Context
import android.webkit.WebSettings
import android.webkit.WebView

internal class CustomWebView(context: Context) : WebView(context) {
    val verticalScrollRange: Int get() = computeVerticalScrollRange() - height

    init {
        settings.userAgentString = WebSettings.getDefaultUserAgent(context)
    }
}