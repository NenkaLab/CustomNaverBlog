package com.nl.customnaverblog.hilt.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

object User {

    @Serializable
    data class Result(
        val isSuccess: Boolean,
        val result: User,
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class User(
        val userId: String,
        val loggedIn: Boolean,
        val blogOwner: Boolean,
        val profileImageUrl: String?,
        @JsonNames("nickName")
        val nickname: String?,
        val hasNewBuddyPost: Boolean,
        val statAllowYn: Boolean,
        val newsCount: Long,
        val writeBlocked: Boolean?,
        @JsonNames("writeBlockYNBySpamIP")
        val writeBlockYnbySpamIp: Boolean,
        @JsonNames("writeBlockYNManuallyBySpamIP")
        val writeBlockYnmanuallyBySpamIp: Boolean,
        val networkBlocked: Boolean,
        val interestYn: Boolean,
        @Contextual val relationType: Any?,
        val isAdult: Boolean,
        val hasDomainId: Boolean,
        val hasOpenedBlog: Boolean,
        val naverId: String,
        val inviteBothBuddy: Boolean,
        val baUserKey: String,
        val todayVisitor: Long,
        val hasNews: Boolean,
        val totalVisitor: Long,
        val marketNotificationOn: Boolean,
        val marketNightNotificationOn: Boolean,
    )

}