package com.nl.customnaverblog.hilt.repo

interface ContentRepository {

    suspend fun getBloggerName(userId: String, logNo: String): String

    suspend fun addDate(userId: String, logNo: String): Long

    suspend fun getTitle(userId: String, logNo: String): String
    suspend fun getTitleBackground(userId: String, logNo: String): String?

    suspend fun getContent(userId: String, logNo: String): String

}