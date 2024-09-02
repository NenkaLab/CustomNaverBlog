package com.nl.customnaverblog.hilt.impl

import android.annotation.SuppressLint
import android.webkit.CookieManager
import com.nl.customnaverblog.Urls
import com.nl.customnaverblog.hilt.data.User
import com.nl.customnaverblog.hilt.repo.UserStatusRepository
import com.nl.customnaverblog.other.Client
import com.nl.customnaverblog.other.HardwareId
import com.nl.customnaverblog.other.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import timber.log.Timber
import javax.inject.Inject

class UserStatusRepositoryImpl @Inject constructor(
    @Client(Urls.Type.BASE) private val httpClient: HttpClient,
    private val jsonSerializer: JsonSerializer,
    @HardwareId private val hardwareId: String,
): UserStatusRepository {

    override suspend fun isLogin(): Boolean {
        return getUser().userId.isNotEmpty()
    }

    override suspend fun getUser(): User.User {
        val json = httpClient.get {
            url(Urls.API_CURRENT_USER)
            val cookie = CookieManager.getInstance().getCookie(Urls.COOKIE_URL)
            Timber.tag("Cookie").d(cookie)
            if (cookie != null) header("Cookie", cookie)
            if (cookie == null || !cookie.contains("ba.uuid")) cookie("ba.uuid", "0")
            if (cookie == null || !cookie.contains("BA_DEVICE")) cookie("BA_DEVICE", hardwareId)
        }.body<String>().trim()

        Timber.tag("getUser").d(json)

        return jsonSerializer.decodeFromString(User.Result.serializer(), json).result
    }

}