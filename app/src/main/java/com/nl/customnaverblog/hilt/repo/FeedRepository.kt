package com.nl.customnaverblog.hilt.repo

import com.nl.customnaverblog.hilt.data.Feed
import com.nl.customnaverblog.hilt.data.Reaction

interface FeedRepository {

    suspend fun getItems(userId: String, groupId: Int, page: Int, itemCount: Int = 10): Feed.List

    suspend fun setLike(userId: String, logNo: Long, guestToken: String, postTime: Long, like: Boolean): Reaction.Result

}