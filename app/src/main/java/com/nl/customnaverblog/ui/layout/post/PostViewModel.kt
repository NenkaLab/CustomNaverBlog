package com.nl.customnaverblog.ui.layout.post

import androidx.lifecycle.ViewModel
import com.nl.customnaverblog.hilt.repo.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val contentRepository: ContentRepository
): ViewModel() {

    suspend fun getBloggerName(userId: String, logNo: String): String {
        return contentRepository.getBloggerName(userId, logNo)
    }

    suspend fun getTitle(userId: String, logNo: String): String {
        return contentRepository.getTitle(userId, logNo)
    }

    suspend fun getTitleBackground(userId: String, logNo: String): String? {
        return contentRepository.getTitleBackground(userId, logNo)
    }

    suspend fun getContent(userId: String, logNo: String): String {
        return contentRepository.getContent(userId, logNo)
    }
}