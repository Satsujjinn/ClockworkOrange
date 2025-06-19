package com.clockworkred.data.repository

import com.clockworkred.domain.StyleRepository
import com.clockworkred.domain.model.StyleProfile
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/** Default implementation of [StyleRepository] backed by in-memory data. */
@Singleton
class StyleRepositoryImpl @Inject constructor() : StyleRepository {
    // TODO Load styles from remote AI-powered service
    private val styles = listOf(
        StyleProfile(
            "beatles",
            "The Beatles",
            "Pioneering Rock & Roll with modal harmonies, tape loops, studio experimentation, vocal double-tracking.",
            listOf("Modal progressions", "Tape loops", "Close harmonies"),
            listOf("Sitar", "Mellotron", "Brass"),
            listOf("Norwegian Wood", "Tomorrow Never Knows")
        ),
        StyleProfile(
            "wall_of_sound",
            "Wall of Sound",
            "Dense orchestral layering, mono echo‐chamber recording, heavy reverb, multiple instruments doubling parts.",
            listOf("Layered guitars", "Echo chambers", "Orchestral stabs"),
            listOf("Multiple guitars", "Strings", "Percussion"),
            listOf("Be My Baby", "River Deep, Mountain High")
        ),
        StyleProfile(
            "brian_wilson",
            "Brian Wilson",
            "Emulative of Spector and Rubber Soul: vocal stacking, percussive bass, dual echo chambers, unconventional instrumentation.",
            listOf("Vocal doubles", "Percussive bass", "Dual echoes"),
            listOf("Saxophones", "Harpsichord", "Vibraphone"),
            listOf("God Only Knows", "Good Vibrations")
        )
    )

    override fun getAllStyles() = flow { emit(styles) }

    override fun getStyle(id: String) = flow { emit(styles.first { it.id == id }) }
}
