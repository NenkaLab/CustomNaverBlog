package com.nl.customnaverblog.ui.layout.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.nl.customnaverblog.hilt.data.Feed
import com.nl.customnaverblog.hilt.data.Reaction
import com.nl.customnaverblog.hilt.repo.FeedRepository
import com.nl.customnaverblog.hilt.repo.UserStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val userStatusRepository: UserStatusRepository
): ViewModel() {

    fun feedPager(userId: String, groupId: Int): Flow<PagingData<Feed.Item>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 8,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                FeedPagingSource(
                    userId = userId,
                    groupId = groupId,
                    onGetFeedList = { userId, groupId, page, loadSize ->
                        feedRepository.getItems(userId, groupId, page, loadSize)
                    }
                )
            }
        ).flow.shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            replay = Int.MAX_VALUE
        )
    }

    suspend fun setLike(userId: String, logNo: Long, guestToken: String, postTime: Long, like: Boolean): Reaction.Result {
        return feedRepository.setLike(userId, logNo, guestToken, postTime, like)
    }

    suspend fun getProfileUrl(): String {
        // null 이여도 그냥 처리
        val imageUrl = userStatusRepository.getUser().profileImageUrl
        return "$imageUrl?type=s1@2x"
    }

    suspend fun getUserId(): String {
        return userStatusRepository.getUser().userId
    }

    suspend fun isLogin() = userStatusRepository.isLogin()

}