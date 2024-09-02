package com.nl.customnaverblog

object Urls {

    enum class Type(val url: String) {
        BASE("m.blog.naver.com"),
        SECTION("section.blog.naver.com"),
        API("apis.naver.com"),
        LOGIN("nid.naver.com"),
        ;

        companion object {
            fun from(url: String): Type? = entries.find { it.url == url }
        }
    }

    const val COOKIE_URL = "https://m.blog.naver.com/"

    const val BLOG_URL = "https://m.blog.naver.com/"

    const val LOGIN_PATH = "nidlogin.login"
    const val LOGIN_QUERY = "?url="
    const val LOGIN_REDIRECT = "https://m.blog.naver.com/Recommendation.naver"
    const val LOGIN_URL = "$LOGIN_PATH$LOGIN_QUERY$LOGIN_REDIRECT"

    const val API_CURRENT_USER_PATH = "api/current-user"
    const val API_CURRENT_USER = API_CURRENT_USER_PATH

    const val API_FEED_PATH = "api/blogs/%s/feed"
    const val API_FEED_QUERY = "?groupId=%d&itemCount=%d&page=%d"
    const val API_FEED = "$API_FEED_PATH$API_FEED_QUERY"

    const val API_LIKE_PATH = "blogserver/like/v1/search/contents"
    const val API_LIKE_QUERY = "?suppress_response_codes=true&pool=blogid&q=BLOG[%s]&isDuplication=true&_=%s"
    const val API_LIKE = "$API_LIKE_PATH$API_LIKE_QUERY"
    
    const val API_SET_LIKE_PATH = "blogserver/like/v1/services/BLOG/contents/%s_%s"
    const val API_SET_LIKE_QUERY = "?suppress_response_codes=true&_method=POST&pool=blogid&displayId=BLOG&reactionType=like&categoryId=post&guestToken=%s&timestamp=%s&_ch=mbw&isDuplication=true&lang=ko&countType=default&count=1&history=&runtimeStatus=&isPostTimeline=false&_=%s"
    const val API_SET_LIKE = "$API_SET_LIKE_PATH$API_SET_LIKE_QUERY"

    const val API_DEL_LIKE_PATH = "blogserver/like/v1/services/BLOG/contents/%s_%s"
    const val API_DEL_LIKE_QUERY = "?suppress_response_codes=true&_method=DELETE&pool=blogid&displayId=BLOG&reactionType=like&categoryId=post&guestToken=%s&timestamp=%s&_ch=mbw&isDuplication=true&lang=ko&countType=default&count=1&history=&runtimeStatus=&_=%s"
    const val API_DEL_LIKE = "$API_DEL_LIKE_PATH$API_DEL_LIKE_QUERY"
}