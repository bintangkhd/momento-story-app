package com.example.storyappsubmission.data

import androidx.paging.PagingData
import com.example.storyappsubmission.data.model.*

object DummyData {
    fun dummySuccessLogin(): LoginResponseModel {
        return LoginResponseModel(
            error = false,
            message = "Login Successfully",
            loginResult = LoginResultResponseModel(
                userId = "userId",
                name = "name",
                token = "token"
            )
        )
    }

    fun dummyErrorLogin(): LoginResponseModel {
        return LoginResponseModel(
            error = true,
            message = "Login Failed! Please recheck email or password."
        )
    }

    fun dummySuccessRegister(): RegisterResponseModel {
        return RegisterResponseModel(
            error = false,
            message = "Register Successfully"
        )
    }

    fun dummyErrorRegister(): RegisterResponseModel {
        return RegisterResponseModel(
            error = true,
            message = "something went wrong! recheck your input and try again later"
        )
    }

    fun dummySuccessStoryCreate(): StoryCreateResponseModel {
        return StoryCreateResponseModel(
            error = false,
            message = "Create Story Successfully"
        )
    }

    fun dummyErrorStoryCreate(): StoryCreateResponseModel {
        return StoryCreateResponseModel(
            error = true,
            message = "Something went wrong while creating story"
        )
    }

    fun dummySuccessStoryList(): StoryResponseModel {
        return StoryResponseModel(
            error = false,
            message = "Load Story List Successfully",
            listStory = arrayListOf(
                StoryListResponseModel(
                    id = "id",
                    name = "name",
                    description = "description",
                    photoUrl = "photoUrl",
                    createdAt = "createdAt",
                    lat = 0.01,
                    lon = 0.01
                )
            )
        )
    }

}