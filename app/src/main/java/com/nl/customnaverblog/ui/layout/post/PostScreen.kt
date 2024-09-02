package com.nl.customnaverblog.ui.layout.post

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.nl.customnaverblog.LocalChromeCustomTabs
import com.nl.customnaverblog.Urls
import com.nl.customnaverblog.ui.components.Loading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    userId: String,
    logNo: String,
    onDismiss: () -> Unit,
    viewModel: PostViewModel = hiltViewModel()
) {

    val density = LocalDensity.current
    val customTab = LocalChromeCustomTabs.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var pullEnabled by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState(
        enabled = { pullEnabled }
    )

    var progress by remember { mutableIntStateOf(0) }

    var blogName by rememberSaveable {
        mutableStateOf("네이버 블로그")
    }
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var titleBg: String? by rememberSaveable {
        mutableStateOf("")
    }
    var html by rememberSaveable {
        mutableStateOf("")
    }
    var blogUrl by rememberSaveable {
        mutableStateOf("")
    }

    val scrollState = rememberScrollState()

    LaunchedEffect(userId, logNo) {
        launch(Dispatchers.IO) {
            blogUrl = Urls.BLOG_URL + "$userId/$logNo"
            blogName = viewModel.getBloggerName(userId, logNo)
            title = viewModel.getTitle(userId, logNo)
            titleBg = viewModel.getTitleBackground(userId, logNo)
            html = viewModel.getContent(userId, logNo)
        }
    }

    BackHandler {
        onDismiss()
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(pullRefreshState.nestedScrollConnection)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = blogName)
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            Box(Modifier.fillMaxSize()) {

                if (html.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .zIndex(2f)
                    ) {
                        AnimatedVisibility(visible = progress != 100) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                progress = { progress.toFloat() }
                            )
                        }

                        /*
                        if (titleBg != null) {
                            Box(
                                modifier = Modifier
                                    .padding(24.dp, 16.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                            ) {
                                TitleBackground(
                                    url = titleBg!!,
                                    modifier = Modifier,
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f))
                                )
                                SelectionContainer {
                                    Text(
                                        modifier = Modifier.padding(16.dp),
                                        text = title,
                                        style = MaterialTheme.typography.headlineMedium,
                                    )
                                }
                            }
                        } else {
                            SelectionContainer {
                                Text(
                                    modifier = Modifier
                                        .padding(24.dp, 16.dp),
                                    text = title,
                                    style = MaterialTheme.typography.headlineMedium,
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp)
                                    .padding(bottom = 8.dp)
                                    .fillMaxWidth()
                            )
                        }
                         */

                        PostWebView(
                            url = blogUrl,
                            html = html,
                            state = pullRefreshState,
                            modifier = Modifier.fillMaxSize(),
                            onLinkClick = { url ->
                                Timber.tag("PostScreen").d("onLinkClick: $url")
                                if (url.startsWith(Urls.BLOG_URL)) {
                                    val pattern = Regex("https://m\\.blog\\.naver\\.com/([^/]+)/([^/]+)")
                                    if (pattern.matches(url)) {
                                        pattern.find(url)?.let {
                                            Timber.tag("PostScreen").d(it.groupValues.joinToString(", "))
                                        }
                                    }
                                } else {
                                    customTab.launch(url)
                                }
                                true
                            },
                            onProgressChange = { progress = it },
                            onScrollY = { pullEnabled = it == 0 }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = html.isEmpty(),
                    enter = fadeIn() + scaleIn(),
                    exit = scaleOut() + fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Loading()
                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(paddingValues)
                        .zIndex(2f),
                )
            }
        }
    )

}