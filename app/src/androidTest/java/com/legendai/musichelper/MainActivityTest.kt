package com.legendai.musichelper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun openSettings_displaysSettingsScreen() {
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.onNodeWithText("Hugging Face API Key").assertIsDisplayed()
    }

    @Test
    fun backFromSettings_returnsToMain() {
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        Espresso.pressBack()
        composeTestRule.onNodeWithText("Generate Song").assertIsDisplayed()
    }

    @Test
    fun generateSong_showsErrorSnackbar() {
        composeTestRule.onNodeWithText("Generate Song").performClick()
        composeTestRule.onNodeWithText("Network error—please retry").assertIsDisplayed()
    }

    @Test
    fun export_showsSavedMessage() {
        // Provide a fake audio response so Export button is visible
        composeTestRule.activityRule.scenario.onActivity { activity ->
            val viewModel = androidx.lifecycle.ViewModelProvider(activity)[MusicViewModel::class.java]
            val cache = activity.cacheDir
            val file = java.io.File(cache, "sample.wav").apply { writeText("data") }
            viewModel.apply {
                val field = this::class.java.getDeclaredField("_audio")
                field.isAccessible = true
                val state = field.get(this) as kotlinx.coroutines.flow.MutableStateFlow<GenerateSongResponse?>
                state.value = GenerateSongResponse(file.absolutePath)
            }
        }
        composeTestRule.onNodeWithText("Export").performClick()
        composeTestRule.onNodeWithText("Saved to").assertIsDisplayed()
    }
}
