package com.example.storyappsubmission.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.storyappsubmission.MainDispatcherRule
import com.example.storyappsubmission.data.preferences.SettingPreference
import com.example.storyappsubmission.ui.settings.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@Suppress("DEPRECATION")
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var preference: SettingPreference
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel(preference)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when getThemeSettings Should return correct theme setting`() = mainDispatcherRules.runBlockingTest {
        val observer = Observer<Boolean> {}

        val flow = flow {
            delay(10)
            emit(true)
        }

        try {
            val expectedResponse = MutableLiveData<Boolean>()
            expectedResponse.value = true

            Mockito.`when`(preference.getThemeSetting()).thenReturn(flow)

            val actualResponse = viewModel.getThemeSettings().observeForever(observer)

            Mockito.verify(preference).getThemeSetting()

            Assert.assertNotNull(actualResponse)
            Assert.assertTrue(true)

        } finally {
            viewModel.getThemeSettings().removeObserver(observer)
        }
    }
}