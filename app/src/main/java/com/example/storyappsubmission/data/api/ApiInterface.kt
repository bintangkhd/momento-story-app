package com.example.storyappsubmission.data.api

import com.example.storyappsubmission.data.model.LoginResponseModel
import com.example.storyappsubmission.data.model.RegisterResponseModel
import com.example.storyappsubmission.data.model.StoryCreateResponseModel
import com.example.storyappsubmission.data.model.StoryResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String, @Field("password") password: String
    ) : LoginResponseModel

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String, @Field("email") email: String, @Field("password") password: String
    ) : RegisterResponseModel

    @GET("stories")
    @Headers("Content-Type:application/json; charset=UTF-8")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : Response<StoryResponseModel>

    @Multipart
    @POST("stories")
    suspend fun createStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double,
        @Part("lon") lon: Double,
    ) : StoryCreateResponseModel

    @GET("stories")
    @Headers("Content-Type:application/json; charset=UTF-8")
    suspend fun getStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ) : StoryResponseModel

}