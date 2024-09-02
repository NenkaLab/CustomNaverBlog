package com.nl.customnaverblog.ui.layout.post

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.nl.customnaverblog.ui.components.CustomWebView
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PostWebView(
    url: String,
    html: String,
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
    onLinkClick: (String) -> Boolean = { true },
    onUrlChange: (String?) -> Unit = {},
    onProgressChange: (progress: Int) -> Unit = {},
    onScrollY: (Int) -> Unit = {},
) {
    val context = LocalContext.current

    val cookieManager = remember { CookieManager.getInstance() }
    cookieManager.setAcceptCookie(true)

    val webView = remember(context) {
        CustomWebView(context).apply {
            isNestedScrollingEnabled = true
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                userAgentString = WebSettings.getDefaultUserAgent(context)
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(this, WebSettingsCompat.FORCE_DARK_ON)
                }
                if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                    WebSettingsCompat.setAlgorithmicDarkeningAllowed(this, true)
                }
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                    WebSettingsCompat.setForceDarkStrategy(
                        this,
                        WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING
                    )
                }
            }
            // TODO : content api 로 로드 성공하면 사용
            // setBackgroundColor(Color.TRANSPARENT)
            setWebViewClient(object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onProgressChange(100)
                    cookieManager.flush()

                    state.endRefresh()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onProgressChange(0)
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    onUrlChange(url)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    if (request == null) return false
                    return onLinkClick(request.url.toString())
                }
            })
            setWebChromeClient(object: WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onProgressChange(newProgress)
                }
            })
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                onScrollY(scrollY)
            }
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    if (state.isRefreshing) {
        LaunchedEffect(true) {
            webView.reload()
        }
    }

    val scrollabeState = rememberScrollableState { delta ->
        val scrollY = webView.scrollY.toFloat()
        val consume = (scrollY - delta).coerceIn(0f, webView.verticalScrollRange.toFloat())
        webView.scrollTo(0, consume.roundToInt())
        (scrollY - webView.scrollY.toFloat())
    }

    AndroidView(
        modifier = Modifier
            .scrollable(scrollabeState, Orientation.Vertical)
            .then(modifier),
        factory = { webView },
        update = {
            it.loadDataWithBaseURL(url, html, "text/html", "UTF-8", null)
        }
    )

    BackHandler(webView.canGoBack()) {
        webView.goBack()
    }
}