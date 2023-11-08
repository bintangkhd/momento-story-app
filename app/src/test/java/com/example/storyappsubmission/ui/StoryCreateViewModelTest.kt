package com.example.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyappsubmission.data.DummyData
import com.example.storyappsubmission.data.getOrAwaitValue
import com.example.storyappsubmission.data.model.StoryCreateResponseModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.data.paging.StoryRepo
import com.example.storyappsubmission.ui.story.storycreate.StoryCreateViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class StoryCreateViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepo: StoryRepo
    private lateinit var viewModel: StoryCreateViewModel
    private var dummyResponse = DummyData.dummySuccessStoryCreate()
    private var dummyDesc = "description".toRequestBody("text/plain".toMediaType())
    private var dummyLat = 0.01
    private var dummyLon = 0.01

    private val file: File = Mockito.mock(File::class.java)
    private val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
    private val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo",
        file.name,
        requestImageFile
    )

    @Before
    fun setUp() {
        viewModel = StoryCreateViewModel(storyRepo)
    }

    @Test
    fun `when Post Story Create Not Null and Return Success`() {
        val expectedCreateStory = MutableLiveData<ResultCondition<StoryCreateResponseModel>>()
        expectedCreateStory.value = ResultCondition.SuccessState(dummyResponse)

        Mockito.`when`(
            storyRepo.createStoryPosting(
                imageFile = imageMultipart,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
        ).thenReturn(expectedCreateStory)

        val actualResponse = viewModel.createStoryPosting(
            imageFile = imageMultipart,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        ).getOrAwaitValue()

        Mockito.verify(storyRepo).createStoryPosting(
            imageFile = imageMultipart,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        )

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.SuccessState)
    }

    @Test
    fun `when Post Story Create Null and Return Error`() {
        dummyResponse = DummyData.dummyErrorStoryCreate()

        val expectedCreateStory = MutableLiveData<ResultCondition<StoryCreateResponseModel>>()
        expectedCreateStory.value = ResultCondition.ErrorState("Something went wrong while creating story")

        Mockito.`when`(
            storyRepo.createStoryPosting(
                imageFile = imageMultipart,
                desc = dummyDesc,
                lat = dummyLat,
                lon = dummyLon
            )
        ).thenReturn(expectedCreateStory)

        val actualResponse = viewModel.createStoryPosting(
            imageFile = imageMultipart,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        ).getOrAwaitValue()

        Mockito.verify(storyRepo).createStoryPosting(
            imageFile = imageMultipart,
            desc = dummyDesc,
            lat = dummyLat,
            lon = dummyLon
        )

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.ErrorState)
    }
}