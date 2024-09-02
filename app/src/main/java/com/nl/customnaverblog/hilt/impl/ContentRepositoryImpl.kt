package com.nl.customnaverblog.hilt.impl

import com.nl.customnaverblog.Urls
import com.nl.customnaverblog.hilt.data.Content
import com.nl.customnaverblog.hilt.data.TitleBackground
import com.nl.customnaverblog.hilt.data.TitleBackgroundResult
import com.nl.customnaverblog.hilt.repo.ContentRepository
import com.nl.customnaverblog.other.Client
import com.nl.customnaverblog.other.HardwareId
import com.nl.customnaverblog.other.JsonSerializer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    @Client(Urls.Type.BASE) private val baseHttpClient: HttpClient,
    private val jsonSerializer: JsonSerializer,
    @HardwareId private val hardwareId: String,
): ContentRepository {

    /// ********************* API 못 찾음 ********************* ///

    private var sameSite: String = ""
    private var cache: String = ""

    private suspend fun get(url: String): Document {
        if (url != sameSite || cache.isEmpty()) {
            val html = baseHttpClient.get {
                url(url)
            }.body<String>().trim()
            cache = html
            sameSite = url
        }

        return Jsoup.parse(cache)
    }

    override suspend fun getBloggerName(userId: String, logNo: String): String {
        val property = get("/$userId/$logNo").getElementById("_post_property")!!
        return property.attr("blogName")
    }

    override suspend fun addDate(userId: String, logNo: String): Long {
        val property = get("/$userId/$logNo").getElementById("_post_property")!!
        return property.attr("addDate").toLong()
    }

    override suspend fun getTitle(userId: String, logNo: String): String {
        val property = get("/$userId/$logNo").getElementById("_photo_view_property")
        if (property != null) {
            return property.attr("title")
        } else {
            val property2 = get("/$userId/$logNo").getElementById("_post_property")!!
            val text = property2.attr("browsertitle")
            val lastIndex = text.lastIndexOf(':')
            val modifiedText = if (lastIndex != -1) text.substring(0, lastIndex) else text
            return modifiedText.trim()
        }
    }

    override suspend fun getTitleBackground(userId: String, logNo: String): String? {
        val property = get("/$userId/$logNo").getElementById("_photo_view_property")
            ?: return null
        val text = property.attr("attachimagepathandidinfo")

        val result = jsonSerializer.decodeFromString(
            TitleBackgroundResult.serializer(), "{\"result\":$text}"
        ).result

        if (result.isEmpty()) return null

        if (result[0].id != 3L) return null

        return result[0].path
    }

    // TODO : content api 로 로드 성공하면 수정
    override suspend fun getContent(userId: String, logNo: String): String {
        // API 가 없어서 html 로 대체
        // 쓸대없는거 제거
        val root = get("/$userId/$logNo")
//        root.select(".post_writer").remove()
//        root.select("#blog_fe_feed").remove()
        root.select("#blog_fe_feed > [class*=footer]").remove()
        root.select(".Ngnb").remove()
        // root.select(".floating_menu").remove()
        // root.select(".blog_authorArea").remove()
        // root.select(".blog_btnArea").remove()
        // root.select(".blog_category").remove()
        root.select(".section_t1").remove()
        // root.select("#_post_area").attr("style", "background: transparent;")
        // root.select("body").attr("style", "background: transparent;")
        // root.select("html").attr("style", "background: transparent;")
        // root.attr("style", "background: transparent;")
        root.select("#_post_area").removeAttr("class")
        root.select("#_post_area .end").removeAttr("id")
        // root.select("head").append("""<meta name="color-scheme" content="light dark">""")
        /*root.select("head").append("""
            |<style>
            |html, body, #_post_area {
            |    background: transparent !important;
            |}
            |
            |:root, html {
            |    color-scheme: light dark !important;
            |}
            |
            |*:not(html, [class*="pzp"]) {
            |    color: currentColor !important;
            |}
            |</style>
        """.trimMargin("|"))*/
        // root.select(".se_component_wrap, .se-section-documentTitle").first()?.remove()
        return root.html()
    }

}