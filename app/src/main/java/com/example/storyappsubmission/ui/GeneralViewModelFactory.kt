package com.example.storyappsubmission.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.data.paging.Injection
import com.example.storyappsubmission.data.paging.StoryRepo
import com.example.storyappsubmission.ui.login.LoginViewModel
import com.example.storyappsubmission.ui.main.MainViewModel
import com.example.storyappsubmission.ui.map.MapViewModel
import com.example.storyappsubmission.ui.register.RegisterViewModel
import com.example.storyappsubmission.ui.story.storycreate.StoryCreateViewModel

class GeneralViewModelFactory private constructor(private val storyRepo: StoryRepo) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(storyRepo) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(storyRepo) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(storyRepo) as T
        }
        if (modelClass.isAssignableFrom(StoryCreateViewModel::class.java)) {
            return StoryCreateViewModel(storyRepo) as T
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(storyRepo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: GeneralViewModelFactory? = null
        fun getInstance(context: Context): GeneralViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: GeneralViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}