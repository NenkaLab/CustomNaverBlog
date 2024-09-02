package com.nl.customnaverblog.ui.layout.auth.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.nl.customnaverblog.Urls
import com.nl.customnaverblog.ui.components.CustomWebView
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginWebView(
    state: PullToRefreshState,
    modifier: Modifier = Modifier,
    onFaviconChange: (Bitmap?) -> Unit = {},
    onTitleChange: (String?) -> Unit = {},
    onUrlChange: (String?) -> Unit = {},
    onProgressChange: (progress: Int) -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onLoginFail: () -> Unit = {},
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
                safeBrowsingEnabled = true
                userAgentString = WebSettings.getDefaultUserAgent(context)
            }
            setWebViewClient(object: WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onProgressChange(100)
                    cookieManager.flush()

                    if (url?.trim()?.startsWith(Urls.LOGIN_REDIRECT) == true) {
                        val cookies = cookieManager.getCookie(url)
                        if (cookies.contains(Regex("(NID_SES|NID_JKL|NID_AUT)"))) {
                            onLoginSuccess()
                        } else {
                            onLoginFail()
                        }
                    }

                    view?.favicon?.let { onFaviconChange(it) }

                    state.endRefresh()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onProgressChange(0)
                    onFaviconChange(favicon ?: view?.favicon)
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    onUrlChange(url)
                }
            })
            setWebChromeClient(object: WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onProgressChange(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    onTitleChange(title)
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

    val scrollableState = rememberScrollableState { delta ->
        val scrollY = webView.scrollY.toFloat()
        val consume = (scrollY - delta).coerceIn(0f, webView.verticalScrollRange.toFloat())
        webView.scrollTo(0, consume.roundToInt())
        (scrollY - webView.scrollY.toFloat())
    }

    AndroidView(
        modifier = Modifier
            .scrollable(scrollableState, Orientation.Vertical)
            .then(modifier),
        factory = { webView },
        update = {
            it.loadUrl("https://${Urls.Type.LOGIN.url}/${Urls.LOGIN_URL}")
        }
    )

    BackHandler(webView.canGoBack()) {
        webView.goBack()
    }
}