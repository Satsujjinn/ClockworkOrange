package com.legendai.musichelper.util

// Utility to suggest simple chord progressions for a given key and genre.
object ChordGenerator {
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
        val root = cleaned.uppercase()
        val index = chromatic.indexOf(root)
        if (index == -1) return null
        return index to minor
    }

    private fun scaleIntervals(minor: Boolean): IntArray {
        return if (minor) intArrayOf(0,2,3,5,7,8,10) else intArrayOf(0,2,4,5,7,9,11)
    }

    private fun chordForDegree(root: Int, minorKey: Boolean, degree: Int, quality: String): String {
        val intervals = scaleIntervals(minorKey)
        val index = (root + intervals[(degree - 1) % intervals.size]) % chromatic.size
        val base = chromatic[index]
        return base + quality
    }

    private fun romanToChord(root: Int, minorKey: Boolean, roman: String): String {
        val upper = roman.uppercase()
        val degree = when {
            upper.startsWith("VII") -> 7
            upper.startsWith("VI") -> 6
            upper.startsWith("V") -> 5
            upper.startsWith("IV") -> 4
            upper.startsWith("III") -> 3
            upper.startsWith("II") -> 2
            else -> 1
        }
        val quality = when {
            roman.contains("dim", true) || upper == "VII" && minorKey -> "dim"
            roman[0].isLowerCase() -> "m"
            else -> ""
        }
        return chordForDegree(root, minorKey, degree, quality)
    }

    fun suggest(key: String, genre: String): List<String> {
        val (root, minor) = parseKey(key) ?: return emptyList()
        val progression = when (genre.lowercase()) {
            "rock" -> listOf("I", "IV", "V", "I")
            "jazz", "jazz_fusion" -> listOf("ii", "V", "I")
            "edm" -> listOf("vi", "IV", "I", "V")
            else -> listOf("I", "V", "vi", "IV")
        }
        return progression.map { romanToChord(root, minor, it) }
    }
}
