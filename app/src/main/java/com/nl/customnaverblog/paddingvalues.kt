package com.nl.customnaverblog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection

context(LayoutDirection)
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val ld = this@LayoutDirection
    val pad = this@plus
    return PaddingValues(
        start = pad.calculateStartPadding(ld) + other.calculateStartPadding(ld),
        top = pad.calculateTopPadding() + other.calculateTopPadding(),
        end = pad.calculateEndPadding(ld) + other.calculateEndPadding(ld),
        bottom = pad.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

context(LayoutDirection)
operator fun PaddingValues.minus(other: PaddingValues): PaddingValues {
    val ld = this@LayoutDirection
    val pad = this@minus
    return PaddingValues(
        start = pad.calculateStartPadding(ld) - other.calculateStartPadding(ld),
        top = pad.calculateTopPadding() - other.calculateTopPadding(),
        end = pad.calculateEndPadding(ld) - other.calculateEndPadding(ld),
        bottom = pad.calculateBottomPadding() - other.calculateBottomPadding()
    )
}