package com.example.storyappsubmission.data.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.example.storyappsubmission.data.api.ApiInterface
import com.example.storyappsubmission.data.model.*
import com.example.storyappsubmission.data.preferences.UserLoginPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepo(private val preference: UserLoginPreference, private val api: ApiInterface) {

    companion object {
        @Volatile
        private var instanceStoryRepo: StoryRepo? = null
        fun getInstance(preference: UserLoginPreference, api: ApiInterface): StoryRepo =
            instanceStoryRepo ?: synchronized(this) {
                instanceStoryRepo ?: StoryRepo(preference, api)
            }.also {
                instanceStoryRepo = it
            }
    }


    fun login(email: String, password: String): LiveData<ResultCondition<LoginResponseModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val response = api.login(
                email,
                password
            )
            if (response.error) {
                emit(ResultCondition.ErrorState(response.message))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    fun register(name: String, email: String, password: String): LiveData<ResultCondition<RegisterResponseModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val response = api.register(
                name,
                email,
                password
            )
            if (response.error) {
                emit(ResultCondition.ErrorState(response.message))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    fun getStoryList(): LiveData<PagingData<StoryListResponseModel>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(preference, api),
            pagingSourceFactory = {
                StoryPagingSource(preference, api)
            }
        ).liveData
    }

    fun getStories(): LiveData<ResultCondition<StoryResponseModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val response = api.getStory(
                token = "Bearer ${preference.getUserLogin().token}",
                page = 1,
                size = 100,
                location = 1
            )
            if (response.error) {
                emit(ResultCondition.ErrorState(response.message))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }

    fun createStoryPosting(imageFile: MultipartBody.Part, desc: RequestBody, lat: Double, lon: Double): LiveData<ResultCondition<StoryCreateResponseModel>> = liveData {
        emit(ResultCondition.LoadingState)
        try {
            val response = api.createStory(
                token = "Bearer ${preference.getUserLogin().token}",
                file = imageFile,
                description = desc,
                lat = lat,
                lon = lon
            )
            if (response.error) {
                emit(ResultCondition.ErrorState(response.message))
            } else {
                emit(ResultCondition.SuccessState(response))
            }
        } catch (e: Exception) {
            emit(ResultCondition.ErrorState(e.message.toString()))
        }
    }
}