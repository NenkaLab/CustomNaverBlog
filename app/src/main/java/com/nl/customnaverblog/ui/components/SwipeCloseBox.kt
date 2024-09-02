package com.nl.customnaverblog.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeCloseBox(
    modifier: Modifier = Modifier,
    onSwipeClosed: () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val state = remember {
        AnchoredDraggableState(
            initialValue = 0f,
            positionalThreshold = { distance -> distance * 0.2f },
            velocityThreshold = { with(density) { 50.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decayAnimationSpec
        )
    }
    var size by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(size) {
        state.updateAnchors(DraggableAnchors {
            0f at 0f
            1f at size.width.toFloat()
        })
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .zIndex(1f)
        .then(modifier)) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    size = coordinates.size
                }
                .anchoredDraggable(
                    state = state,
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(
                        state
                            .requireOffset()
                            .roundToInt(), 0
                    )
                }
        ) {
            content()
        }

        // Dim 표시
        val alpha = state.requireOffset() / size.width
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
                .background(SolidColor(Color.Black.copy(alpha = 0.7f)), alpha = alpha)
        )
    }

    LaunchedEffect(key1 = state.requireOffset()) {
        val offsetX = state.requireOffset()
        if (offsetX <= 0) return@LaunchedEffect
        if (offsetX > size.width / 2) {
            state.anchoredDrag {
                dragTo(size.width.toFloat())
            }
            onSwipeClosed()
        } else {
            state.anchoredDrag {
                dragTo(0f)
            }
            /*animate(offsetX, 0f) { value, _ ->
                state.progress(state.settledValue, 0f)
            }*/
        }
    }
}