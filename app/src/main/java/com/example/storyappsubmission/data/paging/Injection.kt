package com.example.storyappsubmission.data.paging

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyappsubmission.data.api.ConfigApi
import com.example.storyappsubmission.data.preferences.UserLoginPreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storiesin")

object Injection {
    fun provideRepository(context: Context): StoryRepo {
        val preference = UserLoginPreference(context)
        val api = ConfigApi.getApiInterface()
        return StoryRepo.getInstance(preference, api)
    }
}