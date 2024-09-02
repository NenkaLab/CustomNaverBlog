package com.nl.customnaverblog.ui.layout.feed

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.nl.customnaverblog.plus
import com.nl.customnaverblog.ui.components.Loading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    lazyState: LazyListState,
    onPostClick: (userId: String, logNo: Long) -> Unit,
    onBlogClick: (userId: String) -> Unit,
    onCommentClick: (userId: String, logNo: Long) -> Unit,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var profileImage by rememberSaveable { mutableStateOf("") }
    var userId by rememberSaveable { mutableStateOf("") }

    //val lazyState = rememberLazyListState()

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            profileImage = viewModel.getProfileUrl()
            userId = viewModel.getUserId()
            Timber.tag("ProfileImage").d("%s : %s", userId, profileImage)
        }
    }

    val pullRefreshState = rememberPullToRefreshState()

    val pager = if (userId.isNotEmpty()) {
        viewModel.feedPager(userId, 0).collectAsLazyPagingItems()
    } else null

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            pager?.refresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "네이버 블로그")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Rounded.Search, contentDescription = "Search")
                    }

                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        ProfileImage(
                            modifier = Modifier
                                .clickable {

                                },
                            url = profileImage,
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {

                if (pager != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(pullRefreshState.nestedScrollConnection)
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = with(LayoutDirection.Ltr) {
                            paddingValues + PaddingValues(16.dp)
                        },
                        state = lazyState,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(
                            count = pager.itemCount,
                            key = pager.itemKey { item -> "${item.domainIdOrBlogId}_${item.logNo}" },
                            contentType = pager.itemContentType { item ->
                                if (item.hasThumbnail) 1 else 0
                            }
                        ) {
                            if (it == 0 && pullRefreshState.isRefreshing) {
                                SideEffect {
                                    pullRefreshState.endRefresh()
                                }
                            }

                            val item = pager[it] ?: return@items
                            FeedItemCard(
                                item = item,
                                onLike = { i ->
                                    scope.launch(Dispatchers.IO) {
                                        val result = viewModel.setLike(
                                            userId = i.domainIdOrBlogId,
                                            logNo = i.logNo,
                                            guestToken = i.guestToken,
                                            postTime = i.addDate,
                                            like = !i.isSympathy
                                        )
                                        result.isReacted?.let { i.isSympathy = it }
                                        result.count?.let { i.sympathyCount = it }
                                        scope.launch(Dispatchers.Main) {
                                            Toast.makeText(context, result.message ?: "fail", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onComment = { i ->
                                    onCommentClick(i.domainIdOrBlogId, i.logNo)
                                },
                                onClick = { i ->
                                    onPostClick(i.domainIdOrBlogId, i.logNo)
                                },
                                onBlogClick = { i ->
                                    onBlogClick(i.domainIdOrBlogId)
                                }
                            )
                        }
                    }

                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(paddingValues)
                        .zIndex(3f),
                )

                AnimatedVisibility(
                    visible = pager == null,
                    enter = fadeIn() + scaleIn(),
                    exit = scaleOut() + fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Loading()
                }

            }
        }
    )
}