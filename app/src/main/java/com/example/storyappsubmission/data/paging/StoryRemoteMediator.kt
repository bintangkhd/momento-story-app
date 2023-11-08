package com.example.storyappsubmission.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.storyappsubmission.data.api.ApiInterface
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.data.preferences.UserLoginPreference

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(private val preference: UserLoginPreference, private val api: ApiInterface): RemoteMediator<Int, StoryListResponseModel>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryListResponseModel>
    ): MediatorResult {
        val page = 1
        val token = preference.getUserLogin().token.toString()

        try {
            val responseData = token.let { api.getStories(
                "Bearer $it",
                page,
                state.config.pageSize,
                0
            ) }

            return if (responseData.isSuccessful) {
                val endOfPaginationReached = responseData.body()!!.listStory.isEmpty()
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                MediatorResult.Error(Exception("Failed load story"))
            }
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

}