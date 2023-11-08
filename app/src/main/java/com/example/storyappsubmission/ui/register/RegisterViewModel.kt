package com.example.storyappsubmission.ui.register

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission.data.paging.StoryRepo

class RegisterViewModel(private val storyRepo: StoryRepo): ViewModel() {
    fun userPostRegister(name: String, email: String, password: String) = storyRepo.register(name, email, password)
}