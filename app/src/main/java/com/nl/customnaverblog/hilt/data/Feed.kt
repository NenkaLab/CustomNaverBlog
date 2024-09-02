package com.nl.customnaverblog.hilt.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlin.collections.List as KList

object Feed {

    @Serializable
    data class Result(
        val isSuccess: Boolean,
        val result: List,
    )

    @Serializable
    data class List(
        val totalCount: Long,
        val page: Long,
        val items: KList<Item>,
        val isBetterFeedTestTarget: Boolean,
        val aniGifOn: Boolean,
        val newThumbnailOn: Boolean,
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class Item(
        val addDate: Long,
        val allOpenPost: Boolean,
        val blogName: String,
        val blogNo: Long,
        val briefContents: String,
        val commentCount: Long,
        val groupId: Long,
        val hasThumbnail: Boolean,
        val isAppNotificationOn: Boolean,
        val isCommentEnable: Boolean,
        val isLike: Boolean,
        val isMemolog: Boolean,
        val isNotificationOn: Boolean,
        val isRss: Boolean,
        @Contextual val rssUrl: Any?,
        val isScrapEnable: Boolean,
        val isScraped: Boolean,
        val isSympathyEnable: Boolean,
        @JsonNames("isVrthumbnail")
        val isVRThumbnail: Boolean,
        val isVideoThumbnail: Boolean?,
        val videoPlayTime: Long,
        val likeCount: Int,
        @Contextual val linkView: Any?,
        val logNo: Long,
        val logType: Long,
        val nickName: String,
        @Contextual val openGraphView: Any?,
        val isOutSideAllow: Boolean,
        @Contextual val placeName: Any?,
        val profileUrl: String,
        @Contextual val scrap: Any?,
        val scrapType: Long,
        val editorType: Long,
        @Contextual val thisDayPostInfo: Any?,
        val thumbnailCount: Long,
        val thumbnailUrl: String,
        val videoAniThumbnailUrl: String,
        val isPortraitThumbnail: Boolean,
        val thumbnailViewList: KList<Thumbnail>?,
        val title: String,
        val titleWithInspectMessage: String,
        @Contextual val product: Any?,
        val isMarketNotificationOn: Boolean,
        val isMarketNightNotificationOn: Boolean,
        val mp4: Mp4?,
        val domainIdOrBlogId: String,
    ){
        var sympathyCount: Long = 0
        var isSympathy: Boolean = false
        var guestToken: String = ""
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class Thumbnail(
        val thumbnailUrl: String,
        val isVideoThumbnail: Boolean,
        @JsonNames("isVrthumbnail")
        val isVRThumbnail: Boolean,
    )

    @Serializable
    data class Mp4(
        val url: String,
        val height: Long,
        val width: Long,
    )


    object Like {
        @Serializable
        data class Result(
            val contents: KList<Data>,
            val guestToken: String,
        )

        @Serializable
        data class Data(
            val contentsId: String,
            val reactions: KList<Reaction>,
        )

        @Serializable
        data class Reaction(
            val reactionType: String,
            val count: Long,
            val isReacted: Boolean,
        )
    }
}