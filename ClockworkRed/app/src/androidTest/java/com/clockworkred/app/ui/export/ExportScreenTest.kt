package com.clockworkred.app.ui.export

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.clockworkred.app.R
import org.junit.Rule
import org.junit.Test

class ExportScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonsDisplayedAndClickable() {
        composeTestRule.setContent {
            ExportScreen(onDownloadPdf = {}, onSharePdf = {}, onExportMidi = {})
        }
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        composeTestRule.onNodeWithText(ctx.getString(R.string.download_pdf)).assertExists().performClick()
        composeTestRule.onNodeWithText(ctx.getString(R.string.share_pdf)).assertExists().performClick()
        composeTestRule.onNodeWithText(ctx.getString(R.string.export_midi)).assertExists().performClick()
    }
}
