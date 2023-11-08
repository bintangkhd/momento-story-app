package com.example.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyappsubmission.data.DummyData
import com.example.storyappsubmission.data.getOrAwaitValue
import com.example.storyappsubmission.data.model.StoryResponseModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.data.paging.StoryRepo
import com.example.storyappsubmission.ui.map.MapViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepo: StoryRepo
    private lateinit var viewModel: MapViewModel
    private var dummyStory = DummyData.dummySuccessStoryList()

    @Before
    fun setUp() {
        viewModel = MapViewModel(storyRepo)
    }

    @Test
    fun `Condition when Get Story Not Null and Return Success`() {
        val expectedResponse = MutableLiveData<ResultCondition<StoryResponseModel>>()
        expectedResponse.value = ResultCondition.SuccessState(dummyStory)
        Mockito.`when`(storyRepo.getStories()).thenReturn(expectedResponse)

        val actualResponse = viewModel.getStoryLocation().getOrAwaitValue()
        Mockito.verify(storyRepo).getStories()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.SuccessState)
    }

    @Test
    fun `Condition when Get Story Null and Return Error`() {
        dummyStory = DummyData.dummySuccessStoryList()

        val expectedResponse = MutableLiveData<ResultCondition<StoryResponseModel>>()
        expectedResponse.value = ResultCondition.ErrorState("error")
        Mockito.`when`(storyRepo.getStories()).thenReturn(expectedResponse)

        val actualResponse = viewModel.getStoryLocation().getOrAwaitValue()
        Mockito.verify(storyRepo).getStories()

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.ErrorState)
    }
}