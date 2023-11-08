package com.example.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyappsubmission.data.DummyData
import com.example.storyappsubmission.data.getOrAwaitValue
import com.example.storyappsubmission.data.model.RegisterResponseModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.data.paging.StoryRepo
import com.example.storyappsubmission.ui.register.RegisterViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepo: StoryRepo
    private lateinit var viewModel: RegisterViewModel
    private var dummyResponse = DummyData.dummySuccessRegister()
    private val dummyName = "name"
    private val dummyEmail = "email"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        viewModel = RegisterViewModel(storyRepo)
    }

    @Test
    fun `Condition when Post Register Not Null and Return Success`() {
        val expectedRegister = MutableLiveData<ResultCondition<RegisterResponseModel>>()
        expectedRegister.value = ResultCondition.SuccessState(dummyResponse)
        Mockito.`when`(storyRepo.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegister)

        val actualResponse = viewModel.userPostRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepo).register(dummyName, dummyEmail, dummyPassword)

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.SuccessState)
    }

    @Test
    fun `Condition when Post Register Null and Return Error`() {
        dummyResponse = DummyData.dummyErrorRegister()

        val expectedRegister = MutableLiveData<ResultCondition<RegisterResponseModel>>()
        expectedRegister.value = ResultCondition.ErrorState("bad request")
        Mockito.`when`(storyRepo.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedRegister)

        val actualResponse = viewModel.userPostRegister(dummyName, dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepo).register(dummyName, dummyEmail, dummyPassword)

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.ErrorState)
    }
}