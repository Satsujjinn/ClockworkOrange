package com.legendai.musichelper

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.legendai.musichelper.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun openSettings_displaysSettingsScreen() {
        composeTestRule.onNodeWithContentDescription(
            composeTestRule.activity.getString(R.string.settings_icon_desc)
        ).performClick()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.hugging_face_api_key)
        ).assertIsDisplayed()
    }

    @Test
    fun generateSong_showsErrorSnackbar() {
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.generate_song)
        ).performClick()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.error_network)
        ).assertIsDisplayed()
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
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.export)
        ).performClick()
        val savedPrefix = composeTestRule.activity.getString(R.string.saved_to, "")
        composeTestRule.onNodeWithText(savedPrefix.trim(), substring = true).assertIsDisplayed()
    }
}
