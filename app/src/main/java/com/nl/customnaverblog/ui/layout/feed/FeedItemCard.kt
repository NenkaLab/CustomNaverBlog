package com.nl.customnaverblog.ui.layout.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import com.nl.customnaverblog.hilt.data.Feed

@Composable
fun FeedItemCard(
    item: Feed.Item,
    onClick: (Feed.Item) -> Unit,
    onBlogClick: (Feed.Item) -> Unit,
    onLike: (Feed.Item) -> Unit,
    onComment: (Feed.Item) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .clickable { onClick(item) },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BlogUser(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                profileImage = item.profileUrl,
                nickname = item.nickName,
                date = item.addDate,
                onClick = { onBlogClick(item) }
            )
            if (item.hasThumbnail) {
                val thumbnails = remember(item) { item.thumbnailViewList ?: emptyList() }
                val pagerState = rememberPagerState { thumbnails.size }
                Box(modifier = Modifier) {
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        beyondViewportPageCount = 1
                    ) { page ->
                        ThumbnailImage(
                            thumbnails[page].thumbnailUrl + "?type=ffn640_640",
                        )
                    }

                    if (pagerState.pageCount > 1) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(horizontal = 32.dp, vertical = 16.dp)
                                .background(SolidColor(Color(0x80000000)), RoundedCornerShape(50f))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            text = "${pagerState.currentPage + 1}/${pagerState.pageCount}" +
                                    if (pagerState.pageCount != item.thumbnailCount.toInt())
                                        " (${item.thumbnailCount})"
                                    else "",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (item.briefContents.isNotEmpty()) {
                    Text(
                        text = item.briefContents,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.isSympathyEnable) {
                    AssistChip(
                        onClick = {
                            onLike(item)
                        },
                        leadingIcon = {
                            if (item.isSympathy) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Red.copy(alpha = 0.8f)
                                )
                            } else {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    imageVector = Icons.Outlined.Favorite,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        },
                        label = { Text(text = item.sympathyCount.toString()) }
                    )
                }
                if (item.isCommentEnable) {
                    AssistChip(
                        onClick = {
                            onComment(item)
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.AutoMirrored.Rounded.Comment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline
                            )
                        },
                        label = { Text(text = item.commentCount.toString()) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BlogUser(
    profileImage: String,
    date: Long,
    nickname: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileImage(
                url = profileImage,
                size = 32
            )
            Text(
                text = nickname,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = timeAgo(date),
            style = MaterialTheme.typography.labelMedium
        )

    }
}

private fun timeAgo(from: Long, to: Long = System.currentTimeMillis()): String {
    val duration = to - from

    return when {
        duration >= 31_557_600_000L -> "${duration / 31_557_600_000L}년 전" // 1년 = 31,557,600,000 밀리초
        duration >= 2_592_000_000L -> "${duration / 2_592_000_000L}개월 전" // 1개월 = 2,592,000,000 밀리초
        duration >= 86_400_000L -> "${duration / 86_400_000L}일 전" // 1일 = 86,400,000 밀리초
        duration >= 3_600_000L -> "${duration / 3_600_000L}시간 전" // 1시간 = 3,600,000 밀리초
        duration >= 60_000L -> "${duration / 60_000L}분 전" // 1분 = 60,000 밀리초
        else -> "${duration / 1_000L}초 전" // 1초 = 1,000 밀리초
    }
}