package com.example.storyappsubmission.ui.story.storycreate

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission.data.paging.StoryRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody


class StoryCreateViewModel(private val storyRepo: StoryRepo): ViewModel() {

    fun createStoryPosting(imageFile: MultipartBody.Part, desc: RequestBody, lat: Double, lon: Double) = storyRepo.createStoryPosting(imageFile, desc, lat, lon)

}