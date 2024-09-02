package com.nl.customnaverblog.ui.layout.auth.login

import android.graphics.Bitmap
import android.webkit.CookieManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nl.customnaverblog.Urls

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onLoginFail: () -> Unit,
    onCanceled: () -> Unit,
) {

    val context = LocalContext.current

    var title: String? by remember { mutableStateOf(null) }
    var url: String? by remember { mutableStateOf(null) }
    var favicon: Bitmap? by remember { mutableStateOf(null) }
    var progress by remember { mutableIntStateOf(0) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showAll by remember { mutableStateOf(false) }

    var pullEnabled by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState(
        enabled = { pullEnabled }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (favicon == null) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.Language,
                                contentDescription = null
                            )
                        } else {
                            Image(
                                modifier = Modifier.size(24.dp),
                                bitmap = favicon!!.asImageBitmap(),
                                contentDescription = null
                            )
                        }
                        Column {
                            Text(text = title ?: "로그인 하기")
                            AnimatedVisibility(visible = !url.isNullOrEmpty()) {
                                Text(
                                    modifier = Modifier.clickable { showAll = !showAll },
                                    text = url!!,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = if (showAll) Int.MAX_VALUE else 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCanceled) {
                        Icon(imageVector = Icons.Rounded.ArrowBackIosNew, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        CookieManager.getInstance().removeAllCookies {
                            Toast.makeText(context, "쿠키 제거됨", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                AnimatedVisibility(visible = progress != 100) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { progress.toFloat() }
                    )
                }

                Box {

                    LoginWebView(
                        state = pullRefreshState,
                        modifier = Modifier.fillMaxSize()
                            .imePadding(),
                        onLoginSuccess = onLoginSuccess,
                        onLoginFail = onLoginFail,
                        onTitleChange = { title = it },
                        onUrlChange = { url = it },
                        onProgressChange = { progress = it },
                        onFaviconChange = { favicon = it },
                        onScrollY = {
                            pullEnabled = it == 0
                        }
                    )

                    PullToRefreshContainer(
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                }
            }
        }
    )
}