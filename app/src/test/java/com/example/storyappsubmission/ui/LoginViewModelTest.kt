package com.example.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.storyappsubmission.data.DummyData
import com.example.storyappsubmission.data.getOrAwaitValue
import com.example.storyappsubmission.data.model.LoginResponseModel
import com.example.storyappsubmission.data.paging.ResultCondition
import com.example.storyappsubmission.data.paging.StoryRepo
import com.example.storyappsubmission.ui.login.LoginViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepo: StoryRepo
    private lateinit var viewModel: LoginViewModel
    private var dummyResponse = DummyData.dummySuccessLogin()
    private val dummyEmail = "email@gmail.com"
    private val dummyPassword = "password"

    @Before
    fun setUp() {
        viewModel = LoginViewModel(storyRepo)
    }

    @Test
    fun `Condition when Post Login Not Null and Return Success`() {
        val expectedLogin = MutableLiveData<ResultCondition<LoginResponseModel>>()
        expectedLogin.value = ResultCondition.SuccessState(dummyResponse)
        Mockito.`when`(storyRepo.login(dummyEmail, dummyPassword)).thenReturn(expectedLogin)

        val actualResponse = viewModel.userPostLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepo).login(dummyEmail, dummyPassword)

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.SuccessState)
    }

    @Test
    fun `Condition when Post Login Null and Return Error`() {
        dummyResponse = DummyData.dummyErrorLogin()

        val expectedLogin = MutableLiveData<ResultCondition<LoginResponseModel>>()
        expectedLogin.value = ResultCondition.ErrorState("Login Failed! Please recheck email or password.")
        Mockito.`when`(storyRepo.login(dummyEmail, dummyPassword)).thenReturn(expectedLogin)

        val actualResponse = viewModel.userPostLogin(dummyEmail, dummyPassword).getOrAwaitValue()
        Mockito.verify(storyRepo).login(dummyEmail, dummyPassword)

        Assert.assertNotNull(actualResponse)
        Assert.assertTrue(actualResponse is ResultCondition.ErrorState)
    }
}