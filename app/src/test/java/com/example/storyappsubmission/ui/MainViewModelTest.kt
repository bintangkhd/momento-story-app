package com.example.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.example.storyappsubmission.MainDispatcherRule
import com.example.storyappsubmission.data.DummyData
import com.example.storyappsubmission.data.StoryPagingSourceForTest
import com.example.storyappsubmission.data.getOrAwaitValue
import com.example.storyappsubmission.data.model.StoryListResponseModel
import com.example.storyappsubmission.data.noopListUpdateCallback
import com.example.storyappsubmission.data.paging.StoryRepo
import com.example.storyappsubmission.ui.adapter.StoryListAdapter
import com.example.storyappsubmission.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@Suppress("DEPRECATION")
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepo: StoryRepo
    private var dummyStory = DummyData.dummySuccessStoryList()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Condition when Get Story Not Null and Return Success`() = mainDispatcherRules.runBlockingTest {
        val data: PagingData<StoryListResponseModel> = StoryPagingSourceForTest.snapshot(dummyStory.listStory)

        val expectedResponse = MutableLiveData<PagingData<StoryListResponseModel>>()
        expectedResponse.value = data
        Mockito.`when`(storyRepo.getStoryList()).thenReturn(expectedResponse)

        val viewModel = MainViewModel(storyRepo)
        val actualStory: PagingData<StoryListResponseModel> = viewModel.getListStory.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFFERENT_STORY,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.listStory, differ.snapshot())
        Assert.assertEquals(dummyStory.listStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory.listStory[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Condition when Get Story Null and Return Error`() = mainDispatcherRules.runBlockingTest {

        val expectedResponse = MutableLiveData<PagingData<StoryListResponseModel>>(PagingData.empty())
        Mockito.`when`(storyRepo.getStoryList()).thenReturn(expectedResponse)

        val viewModel = MainViewModel(storyRepo)
        val actualStory: PagingData<StoryListResponseModel> = viewModel.getListStory.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFFERENT_STORY,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

