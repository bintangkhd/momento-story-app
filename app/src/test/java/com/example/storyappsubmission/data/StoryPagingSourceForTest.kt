package com.example.storyappsubmission.data

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyappsubmission.data.model.StoryListResponseModel

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

class StoryPagingSourceForTest : PagingSource<Int, LiveData<List<StoryListResponseModel>>>() {
    companion object {
        fun snapshot(items: List<StoryListResponseModel>): PagingData<StoryListResponseModel> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryListResponseModel>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryListResponseModel>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

