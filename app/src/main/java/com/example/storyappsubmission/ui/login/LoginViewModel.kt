package com.example.storyappsubmission.ui.login

import androidx.lifecycle.ViewModel
import com.example.storyappsubmission.data.paging.StoryRepo

class LoginViewModel(private val storyRepo: StoryRepo): ViewModel() {

    fun userPostLogin(email: String, password: String) = storyRepo.login(email, password)

}

