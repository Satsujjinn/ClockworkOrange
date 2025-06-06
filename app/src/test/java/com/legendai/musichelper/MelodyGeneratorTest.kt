package com.legendai.musichelper

import com.legendai.musichelper.util.MelodyGenerator
import org.junit.Test
import kotlin.test.assertTrue

class MelodyGeneratorTest {
    @Test
    fun generateGuitarMelody() {
        val tab = MelodyGenerator.generate("C", 8, MelodyGenerator.Instrument.GUITAR)
        assertTrue(tab.isNotEmpty())
    }

    @Test
    fun generateKeyboardMelody() {
        val notes = MelodyGenerator.generate("Am", 8, MelodyGenerator.Instrument.KEYBOARD)
        assertTrue(notes.isNotEmpty())
    }

    @Test
    fun generateBassMelody() {
        val tab = MelodyGenerator.generate("E", 8, MelodyGenerator.Instrument.BASS)
        assertTrue(tab.isNotEmpty())
    }
}
