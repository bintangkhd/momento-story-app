package com.example.storyappsubmission.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.data.paging.StoryRepo

class MainViewModel(storyRepo: StoryRepo): ViewModel() {
    val getListStory: LiveData<PagingData<StoryListResponseModel>> =
        storyRepo.getStoryList().cachedIn(viewModelScope)
}