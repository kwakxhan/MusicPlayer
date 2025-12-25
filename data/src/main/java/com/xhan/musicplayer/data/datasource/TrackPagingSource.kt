package com.xhan.musicplayer.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.xhan.musicplayer.domain.model.Track

class TrackPagingSource(
    private val mediaStoreDataSource: MediaStoreDataSource
) : PagingSource<Int, Track>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Track> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val offset = page * pageSize

            val tracks = mediaStoreDataSource.getPagedTracks(offset, pageSize)

            LoadResult.Page(
                data = tracks,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (tracks.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Track>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}