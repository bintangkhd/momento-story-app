package com.example.storyappsubmission.ui.story.storydetail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.storyappsubmission.R
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.databinding.ActivityStoryDetailBinding
import com.example.storyappsubmission.utils.withDateFormat

@Suppress("DEPRECATION")
class StoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORY_DETAIL = "extra_story_detail"
    }

    private lateinit var binding: ActivityStoryDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getIntent = intent.getParcelableExtra<StoryListResponseModel>(EXTRA_STORY_DETAIL) as StoryListResponseModel

        Glide.with(this@StoryDetailActivity)
            .load(getIntent.photoUrl)
            .fitCenter()
            .into(binding.ivStory)

        getIntent.apply {
            val postedByName = String.format(getString(R.string.posted_by), name)
            val spannable = SpannableString(Html.fromHtml(postedByName))
            val colorSpan = ForegroundColorSpan(getResources().getColor(R.color.main_green))
            val startIndex = postedByName.indexOf(name)
            val endIndex = startIndex + name.length
            spannable.setSpan(colorSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setupToolbar(name)

            binding.tvPostedBy.setText(spannable)
            binding.tvOnDate.setText(String.format(getString(R.string.on_date), createdAt.withDateFormat()))
            binding.tvDescription.setText(description)
        }
    }

    private fun setupToolbar(name: String) {
        title = name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }


}