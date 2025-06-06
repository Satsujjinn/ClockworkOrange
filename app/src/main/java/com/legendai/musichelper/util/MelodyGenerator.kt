package com.legendai.musichelper.util

import kotlin.random.Random

/**
 * Utility generating very simple melodies as ASCII tab.
 * This is a lightweight implementation using basic music theory
 * to avoid adding external dependencies.
 */
object MelodyGenerator {
    enum class Instrument { GUITAR, KEYBOARD, BASS }

    private val chromatic = listOf(
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"
    )

    private fun parseKey(key: String): Pair<Int, Boolean>? {
        var cleaned = key.trim()
            .replace("minor", "m", ignoreCase = true)
            .replace("major", "", ignoreCase = true)
            .trim()
        if (cleaned.isEmpty()) return null
        val minor = cleaned.endsWith("m")
        if (minor) cleaned = cleaned.removeSuffix("m").trim()
        var root = cleaned.uppercase()
        root = when (root) {
            "DB" -> "C#"
            , "EB" -> "D#"
            , "GB" -> "F#"
            , "AB" -> "G#"
            , "BB" -> "A#"
            else -> root
        }
        val index = chromatic.indexOf(root)
        if (index == -1) return null
        return index to minor
    }

    private fun scaleIntervals(minor: Boolean) =
        if (minor) intArrayOf(0,2,3,5,7,8,10) else intArrayOf(0,2,4,5,7,9,11)

    // ----- Guitar specific helpers -----
    private val tuning = listOf("E4", "B3", "G3", "D3", "A2", "E2") // high to low
    private val tuningIdx = tuning.map { noteIndex(it) }

    // ----- Bass specific helpers -----
    private val bassTuning = listOf("G2", "D2", "A1", "E1") // high to low
    private val bassTuningIdx = bassTuning.map { noteIndex(it) }

    private fun noteIndex(note: String): Int {
        val m = Regex("([A-G]#?)(\\d)").matchEntire(note) ?: return 0
        val name = m.groupValues[1]
        val octave = m.groupValues[2].toInt()
        val base = chromatic.indexOf(name)
        return octave * 12 + base
    }

    private fun findStringAndFret(pitch: Int): Pair<Int, Int> {
        for (i in tuningIdx.indices) {
            val fret = pitch - tuningIdx[i]
            if (fret in 0..12) return i to fret
        }
        val fret = (pitch - tuningIdx[0]).coerceIn(0,12)
        return 0 to fret
    }

    private fun findBassStringAndFret(pitch: Int): Pair<Int, Int> {
        for (i in bassTuningIdx.indices) {
            val fret = pitch - bassTuningIdx[i]
            if (fret in 0..12) return i to fret
        }
        val fret = (pitch - bassTuningIdx[0]).coerceIn(0,12)
        return 0 to fret
    }

    private fun generateGuitarTab(root: Int, minor: Boolean, length: Int): List<String> {
        val scale = scaleIntervals(minor).map { (root + it) % 12 }
        val lines = MutableList(6) { StringBuilder() }
        repeat(length) {
            val degree = scale.random()
            val octave = 4 + Random.nextInt(0,2)
            val pitch = degree + octave*12
            val (stringIdx, fret) = findStringAndFret(pitch)
            for (i in 0 until 6) {
                if (i == stringIdx) {
                    lines[i].append(fret.toString().padStart(2,'-')).append("-")
                } else {
                    lines[i].append("---")
                }
            }
        }
        val names = listOf("e|","B|","G|","D|","A|","E|")
        return lines.indices.map { names[it] + lines[it].toString() }
    }

    private fun generateBassTab(root: Int, minor: Boolean, length: Int): List<String> {
        val scale = scaleIntervals(minor).map { (root + it) % 12 }
        val lines = MutableList(4) { StringBuilder() }
        repeat(length) {
            val degree = scale.random()
            val octave = 2 + Random.nextInt(0,2)
            val pitch = degree + octave*12
            val (stringIdx, fret) = findBassStringAndFret(pitch)
            for (i in 0 until 4) {
                if (i == stringIdx) {
                    lines[i].append(fret.toString().padStart(2,'-')).append("-")
                } else {
                    lines[i].append("---")
                }
            }
        }
        val names = listOf("G|","D|","A|","E|")
        return lines.indices.map { names[it] + lines[it].toString() }
    }

    private fun generateKeyboard(root: Int, minor: Boolean, length: Int): List<String> {
        val scale = scaleIntervals(minor).map { (root + it) % 12 }
        val notes = MutableList(length) {
            val degree = scale.random()
            chromatic[degree] + "4"
        }
        return listOf(notes.joinToString(" "))
    }

    /**
     * Generate a short melody in [key] for the given [instrument].
     * The result is returned as a list of strings ready to display.
     */
    fun generate(key: String, length: Int = 16, instrument: Instrument = Instrument.GUITAR): List<String> {
        val (root, minor) = parseKey(key) ?: return listOf("Invalid key")
        return when(instrument) {
            Instrument.GUITAR -> generateGuitarTab(root, minor, length)
            Instrument.KEYBOARD -> generateKeyboard(root, minor, length)
            Instrument.BASS -> generateBassTab(root, minor, length)
        }
    }
}
