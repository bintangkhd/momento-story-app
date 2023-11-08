package com.example.storyappsubmission.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyappsubmission.data.api.ApiInterface
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.data.preferences.UserLoginPreference

class StoryPagingSource(private val preference: UserLoginPreference, private val api: ApiInterface): PagingSource<Int, StoryListResponseModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryListResponseModel> {
        return try {
            val page = params.key ?: 1
            val token = preference.getUserLogin().token.toString()

            if (token.isNotEmpty()) {
                val responseData = token.let { api.getStories("Bearer $it", page, params.loadSize, 0) }
                if (responseData.isSuccessful) {
                    LoadResult.Page(
                        data = responseData.body()?.listStory ?: emptyList(),
                        prevKey = if (page == 1) null else page -1,
                        nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else page + 1
                    )
                } else {
                    LoadResult.Error(Exception("Failed load story"))
                }
            } else {
                LoadResult.Error(Exception("Token empty"))
            }
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryListResponseModel>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}