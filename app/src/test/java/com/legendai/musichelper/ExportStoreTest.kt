package com.legendai.musichelper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportStoreTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteSharedPreferences("exports")
    }

    @After
    fun tearDown() {
        context.deleteSharedPreferences("exports")
    }

    @Test
    fun addStoresEntry() {
        ExportStore.add(context, "one.wav")

        val prefs = context.getSharedPreferences("exports", Context.MODE_PRIVATE)
        val entries = prefs.getStringSet("entries", emptySet())!!

        assertEquals(1, entries.size)
        assertTrue(entries.first().startsWith("one.wav|"))
    }

    @Test
    fun listReturnsEntriesSorted() {
        ExportStore.add(context, "first.wav")
        Thread.sleep(10)
        ExportStore.add(context, "second.wav")

        val list = ExportStore.list(context)

        assertEquals(2, list.size)
        assertEquals("second.wav", list[0].fileName)
        assertEquals("first.wav", list[1].fileName)
        assertTrue(list[0].time >= list[1].time)
    }

    @Test
    fun removeDeletesEntry() {
        ExportStore.add(context, "a.wav")
        ExportStore.add(context, "b.wav")

        ExportStore.remove(context, "a.wav")

        val list = ExportStore.list(context)

        assertEquals(1, list.size)
        assertEquals("b.wav", list[0].fileName)
    }
}
