package com.example.storyappsubmission.data.preferences

import android.content.Context
import com.example.storyappsubmission.data.model.LoginModel

class UserLoginPreference(mContext: Context) {

    companion object {
        private const val NAME = "name"
        private const val USER_ID = "userId"
        private const val TOKEN = "token"
        private const val PREFERENCE_NAME = "login_pref"
    }

    private val mPreference = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setUserLogin(user: LoginModel) {
        val prefEdit = mPreference.edit()
        prefEdit.putString(NAME, user.name)
        prefEdit.putString(USER_ID, user.userId)
        prefEdit.putString(TOKEN, user.token)
        prefEdit.apply()
    }

    fun getUserLogin(): LoginModel {
        val name = mPreference.getString(NAME, null)
        val userId = mPreference.getString(USER_ID, null)
        val token = mPreference.getString(TOKEN, null)

        return LoginModel(userId, name, token)
    }

    fun userLogout() {
        val editor = mPreference.edit().clear()
        editor.apply()
    }

}