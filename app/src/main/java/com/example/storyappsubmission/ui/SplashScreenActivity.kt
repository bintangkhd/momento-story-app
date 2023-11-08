package com.example.storyappsubmission.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.data.model.LoginModel
import com.example.storyappsubmission.data.preferences.SettingPreference
import com.example.storyappsubmission.data.preferences.UserLoginPreference
import com.example.storyappsubmission.databinding.ActivitySplashScreenBinding
import com.example.storyappsubmission.ui.login.LoginActivity
import com.example.storyappsubmission.ui.main.MainActivity
import com.example.storyappsubmission.ui.settings.SettingsViewModel
import com.example.storyappsubmission.ui.settings.SettingsViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var preference: UserLoginPreference
    private lateinit var loginModel: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = UserLoginPreference(this)
        loginModel = preference.getUserLogin()

        navigateTo()
    }

    private fun navigateTo() {
        if (loginModel.name != null && loginModel.userId != null && loginModel.token != null) {
            val intent = Intent(this, MainActivity::class.java)
            settingTheme()
            intentSplash(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            settingTheme()
            intentSplash(intent)
        }
    }

    private fun intentSplash(intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, 1500)
    }

    private fun settingTheme() {
        val pref = SettingPreference.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(this, SettingsViewModelFactory(pref)).get(
            SettingsViewModel::class.java
        )
        settingViewModel.getThemeSettings().observe(this
        ) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }
    }

}