package com.legendai.musichelper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExportStoreTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        File(context.filesDir, "datastore/exports.json").delete()
    }

    @After
    fun tearDown() {
        File(context.filesDir, "datastore/exports.json").delete()
    }

    @Test
    fun addStoresEntry() = runTest {
        ExportStore.add(context, "one.wav")

        val list = ExportStore.list(context)

        assertEquals(1, list.size)
        assertEquals("one.wav", list[0].fileName)
        assertTrue(list[0].time > 0)
    }

    @Test
    fun listReturnsEntriesSorted() = runTest {
        ExportStore.add(context, "first.wav")
        delay(10)
        ExportStore.add(context, "second.wav")

        val list = ExportStore.list(context)

        assertEquals(2, list.size)
        assertEquals("second.wav", list[0].fileName)
        assertEquals("first.wav", list[1].fileName)
        assertTrue(list[0].time >= list[1].time)
    }

    @Test
    fun addFileNameWithPipe() = runTest {
        ExportStore.add(context, "file|name.wav")

        val list = ExportStore.list(context)

        assertEquals(1, list.size)
        assertEquals("file|name.wav", list[0].fileName)
    }

    @Test
    fun removeDeletesEntry() = runTest {
        ExportStore.add(context, "a.wav")
        ExportStore.add(context, "b.wav")

        ExportStore.remove(context, "a.wav")

        val list = ExportStore.list(context)

        assertEquals(1, list.size)
        assertEquals("b.wav", list[0].fileName)
    }
}
