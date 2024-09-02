package com.nl.customnaverblog.ui.layout.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import com.nl.customnaverblog.ui.theme.shimmer
import timber.log.Timber

@Composable
fun ProfileImage(
    url: String,
    modifier: Modifier = Modifier,
    size: Int = 42,
) {
    val context = LocalContext.current
    SubcomposeAsyncImage(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(50))
            .then(modifier),
        model = ImageRequest.Builder(context)
            .data(url)
            .size(size, size)
            .build(),
        contentDescription = null
    ) {
        val state by painter.state.collectAsStateWithLifecycle()
        when (state) {
            is AsyncImagePainter.State.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                        .shimmer()
                )
            }
            is AsyncImagePainter.State.Error -> {
                ProfileImageNotLoggedIn(
                    modifier = Modifier.fillMaxSize(),
                    size = size
                )
                Timber.tag("ProfileImage").e((state as AsyncImagePainter.State.Error).result.throwable)
            }

            else -> {
                SubcomposeAsyncImageContent(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
        }
    }
}

@Composable
fun ProfileImageNotLoggedIn(
    modifier: Modifier = Modifier,
    size: Int = 42
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.Gray)
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}