package com.nl.customnaverblog.hilt.impl

import android.webkit.CookieManager
import com.nl.customnaverblog.Urls
import com.nl.customnaverblog.hilt.data.Feed
import com.nl.customnaverblog.hilt.data.Reaction
import com.nl.customnaverblog.hilt.repo.FeedRepository
import com.nl.customnaverblog.other.Client
import com.nl.customnaverblog.other.HardwareId
import com.nl.customnaverblog.other.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import timber.log.Timber
import javax.inject.Inject

class FeedRepositoryImpl @Inject constructor(
    @Client(Urls.Type.BASE) private val baseHttpClient: HttpClient,
    @Client(Urls.Type.API) private val apiHttpClient: HttpClient,
    private val jsonSerializer: JsonSerializer,
    @HardwareId private val hardwareId: String,
): FeedRepository {

    override suspend fun getItems(
        userId: String,
        groupId: Int,
        page: Int,
        itemCount: Int
    ): Feed.List {
        val feedJson = baseHttpClient.get {
            url(Urls.API_FEED.format(userId, groupId, itemCount, page))
            val cookie = CookieManager.getInstance().getCookie(Urls.COOKIE_URL)
            if (cookie != null) header("Cookie", cookie)
            if (cookie == null || !cookie.contains("ba.uuid")) cookie("ba.uuid", "0")
            if (cookie == null || !cookie.contains("BA_DEVICE")) cookie("BA_DEVICE", hardwareId)
        }.body<String>().trim()

        val feed = jsonSerializer.decodeFromString(Feed.Result.serializer(), feedJson).result

        val likeRequest = feed.items.joinToString(",") { "${it.domainIdOrBlogId}_${it.logNo}" }

        val likeJson = apiHttpClient.get {
            url(Urls.API_LIKE.format(likeRequest, System.currentTimeMillis().toString()))
            val cookie = CookieManager.getInstance().getCookie(Urls.COOKIE_URL)
            if (cookie != null) header("Cookie", cookie)
            if (cookie == null || !cookie.contains("ba.uuid")) cookie("ba.uuid", "0")
            if (cookie == null || !cookie.contains("BA_DEVICE")) cookie("BA_DEVICE", hardwareId)
        }.body<String>().trim()

        val like = jsonSerializer.decodeFromString(Feed.Like.Result.serializer(), likeJson)

        like.contents.forEach {
            val likeItem = feed.items.find { item -> "${item.domainIdOrBlogId}_${item.logNo}" == it.contentsId }
            if (likeItem != null) {
                val reaction = it.reactions.find { reaction -> reaction.reactionType == "like" } ?: return@forEach
                likeItem.sympathyCount = reaction.count
                likeItem.isSympathy = reaction.isReacted
                likeItem.guestToken = like.guestToken
            }
        }

        return feed
    }

    override suspend fun setLike(userId: String, logNo: Long, guestToken: String, postTime: Long, like: Boolean): Reaction.Result {
        val time = System.currentTimeMillis().toString()
        val time2 = postTime.toString()

        val json = apiHttpClient.get {
            if (like) {
                url(Urls.API_SET_LIKE.format(userId, logNo.toString(), guestToken, time, time2))
            } else {
                url(Urls.API_DEL_LIKE.format(userId, logNo.toString(), guestToken, time, time2))
            }
            val cookie = CookieManager.getInstance().getCookie(Urls.COOKIE_URL)
            if (cookie != null) header("Cookie", cookie)
            if (cookie == null || !cookie.contains("ba.uuid")) cookie("ba.uuid", "0")
            if (cookie == null || !cookie.contains("BA_DEVICE")) cookie("BA_DEVICE", hardwareId)
        }.body<String>().trim()

        Timber.tag("setLike").d(json)

        return jsonSerializer.decodeFromString(Reaction.Result.serializer(), json)
    }

}