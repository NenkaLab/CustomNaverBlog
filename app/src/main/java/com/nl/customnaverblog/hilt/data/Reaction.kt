package com.nl.customnaverblog.hilt.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

object Reaction {
    @Serializable
    data class Result(
        val serviceId: String?,
        val displayId: String?,
        val contentsId: String?,
        val message: String?,
        val categoryId: String?,
        val reactionType: String?,
        val count: Long?,
        val isReacted: Boolean?,
        val countType: String?,
        val nolimitUserCount: Long?,
        val statusCode: Long?,
        val errorCode: Long?,
        @Contextual val moreInfos: Any?,
        val logMessage: String?,
    )
}