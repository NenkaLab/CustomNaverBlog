package com.nl.customnaverblog.hilt.data

import kotlinx.serialization.Serializable

data class Content(
    val isImage: Boolean,
    val isVideo: Boolean,
    val isText: Boolean,
    val value: String
)

@Serializable
data class TitleBackgroundResult(
    val result: List<TitleBackground>,
)

@Serializable
data class TitleBackground(
    val path: String,
    val id: Long,
)

data object Test_123883821838123 {}