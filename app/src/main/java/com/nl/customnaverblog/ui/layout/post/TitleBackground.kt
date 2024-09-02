package com.nl.customnaverblog.ui.layout.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
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
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import com.nl.customnaverblog.ui.theme.shimmer

@Composable
internal fun TitleBackground(
    url: String,
    modifier: Modifier = Modifier,
) {

    val context = LocalContext.current
    SubcomposeAsyncImage(
        modifier = Modifier
            .then(modifier)
            .aspectRatio(16f/10f)
            .clip(RoundedCornerShape(16.dp)),
        model = ImageRequest.Builder(context)
            .data(url)
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
                TitleBackgroundFail(
                    modifier = Modifier.fillMaxSize()
                )
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
internal fun TitleBackgroundFail(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(42.dp),
            imageVector = Icons.Rounded.BrokenImage,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}