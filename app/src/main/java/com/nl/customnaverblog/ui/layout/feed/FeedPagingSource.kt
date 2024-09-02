package com.nl.customnaverblog.ui.layout.feed

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nl.customnaverblog.hilt.data.Feed
import timber.log.Timber

class FeedPagingSource(
    private val userId: String,
    private val groupId: Int,
    private val onGetFeedList: suspend (String, Int, Int, Int) -> Feed.List,
): PagingSource<Int, Feed.Item>() {

    override fun getRefreshKey(state: PagingState<Int, Feed.Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed.Item> {
        val page = params.key ?: 0

        return try {
            val list = onGetFeedList(userId, groupId, page, params.loadSize)

            if (list.page == 0L) {
                Timber.tag("FeedPagingSource").d("0-${list.totalCount} duplicate")
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = page + 1
                )
            }

            Timber.tag("FeedPagingSource").d("%s", list.items.joinToString("\n") { "${list.page}-${list.totalCount} ${it.domainIdOrBlogId}_${it.logNo} : ${it.title}" })

            LoadResult.Page(
                data = list.items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

}