package com.clockworkred.app.ui.projects

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import com.clockworkred.app.R
import org.junit.Rule
import org.junit.Test

/** Basic UI test verifying project list empty state. */
class ProjectsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsEmptyState() {
        composeTestRule.setContent {
            ProjectsScreen(navController = rememberNavController())
        }
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.onNodeWithText(ctx.getString(R.string.no_projects)).assertExists()
        composeTestRule.onNodeWithText(ctx.getString(R.string.new_project)).performClick()
    }
}
