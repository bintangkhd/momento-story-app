package com.example.storyappsubmission.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.databinding.ItemRowStoryBinding
import com.example.storyappsubmission.ui.story.storydetail.StoryDetailActivity
import com.example.storyappsubmission.utils.withDateFormat

class StoryListAdapter
    : PagingDataAdapter<StoryListResponseModel, StoryListAdapter.ListViewHolder>(DIFFERENT_STORY) {

    companion object {
        val DIFFERENT_STORY = object : DiffUtil.ItemCallback<StoryListResponseModel>() {
            override fun areItemsTheSame(oldItem: StoryListResponseModel, newItem: StoryListResponseModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryListResponseModel, newItem: StoryListResponseModel): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class ListViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryListResponseModel) {
            with(binding) {
                tvName.text = story.name
                tvDate.text = story.createdAt.withDateFormat()
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .into(ivStory)

                cvStory.setOnClickListener {
                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra(StoryDetailActivity.EXTRA_STORY_DETAIL, story)
                    itemView.context.startActivity(
                        intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(binding.root.context as Activity).toBundle()
                    )
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(story = data)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        return ListViewHolder(
            ItemRowStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

}