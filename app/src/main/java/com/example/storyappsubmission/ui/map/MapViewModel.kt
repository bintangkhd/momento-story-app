package com.example.storyappsubmission.ui.map

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission.data.paging.StoryRepo

class MapViewModel(private val storyRepo: StoryRepo) : ViewModel() {
    fun getStoryLocation() = storyRepo.getStories()
}