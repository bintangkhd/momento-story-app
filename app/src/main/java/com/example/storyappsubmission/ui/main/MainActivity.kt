package com.example.storyappsubmission.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyappsubmission.R
import com.example.storyappsubmission.data.model.LoginModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.data.preferences.UserLoginPreference
import com.example.storyappsubmission.databinding.ActivityMainBinding
import com.example.storyappsubmission.ui.GeneralViewModelFactory
import com.example.storyappsubmission.ui.adapter.LoadingStateAdapter
import com.example.storyappsubmission.ui.settings.SettingsActivity
import com.example.storyappsubmission.ui.adapter.StoryListAdapter
import com.example.storyappsubmission.ui.login.LoginActivity
import com.example.storyappsubmission.ui.map.MapActivity
import com.example.storyappsubmission.ui.story.storycreate.StoryCreateActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { factory }
    private lateinit var adapter: StoryListAdapter
    private lateinit var preference: UserLoginPreference
    private lateinit var loginModel: LoginModel
    private lateinit var factory: GeneralViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.elevation = 0f

        factory = GeneralViewModelFactory.getInstance(binding.root.context)

        preference = UserLoginPreference(this)
        loginModel = preference.getUserLogin()

        createStoryRedirect()
        getNameUser()

        loadStoryList(binding.root.context)
        getListStory()
        logoutUser()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.language -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(intent)
            }
            R.id.maps -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAffinity()
            System.exit(0)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getNameUser() {
        binding.tvName.text = loginModel.name
    }


    private fun loadStoryList(context: Context) {
        val rvStory = binding.rvListStory

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvStory.layoutManager = GridLayoutManager(context, 2)
        } else {
            rvStory.layoutManager = LinearLayoutManager(context)
        }

        adapter = StoryListAdapter()
        rvStory.adapter = adapter

    }

    private fun getListStory() {
        viewModel.getListStory.observe(this) {
            adapter.submitData(lifecycle, it)
        }

        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.addLoadStateListener { loadState ->
            val isLoading = loadState.refresh is LoadState.Loading
            progressLoading(isLoading)
        }

    }

    private fun createStoryRedirect() {
        binding.buttonCreateStory.setOnClickListener {
            val intent = Intent(this@MainActivity, StoryCreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logoutUser() {
        binding.ivLogout.setOnClickListener {
            preference.userLogout()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun progressLoading(loading: Boolean) {
        if (loading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.rvListStory.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.rvListStory.visibility = View.VISIBLE
        }
    }

}