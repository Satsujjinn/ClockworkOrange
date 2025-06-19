package com.clockworkred.data.repository

import com.clockworkred.domain.model.TheoryNote
import com.clockworkred.domain.model.TheoryTopic
import com.clockworkred.domain.repository.TheoryRepository
import javax.inject.Inject
import javax.inject.Singleton

/** Simple in-memory implementation of [TheoryRepository]. */
@Singleton
class TheoryRepositoryImpl @Inject constructor() : TheoryRepository {
    private val notes = mapOf(
        TheoryTopic.SCALE to TheoryNote(
            title = "Scales",
            description = "A scale is a sequence of notes in ascending or descending order.",
            examples = listOf("C Major: C D E F G A B", "A Minor: A B C D E F G")
        ),
        TheoryTopic.MODE to TheoryNote(
            title = "Modes",
            description = "Modes are variations of scales with unique tonalities.",
            examples = listOf("Dorian: starting on the second degree of the major scale")
        ),
        TheoryTopic.CHORD_FUNCTION to TheoryNote(
            title = "Chord Functions",
            description = "Chord functions describe the role a chord plays in harmony.",
            examples = listOf("Tonic - the home chord", "Dominant - creates tension")
        ),
        TheoryTopic.RHYTHM_PATTERN to TheoryNote(
            title = "Rhythm Patterns",
            description = "Rhythm patterns organize beats into repeating units.",
            examples = listOf("Backbeat on 2 and 4", "Bossa nova clave")
        )
    )

    override suspend fun fetchNoteFor(topic: TheoryTopic): TheoryNote {
        // TODO replace with AI-powered fetch or JSON-backed local data
        return notes[topic] ?: TheoryNote("Unknown", "No information available", emptyList())
    }
}
