package com.clockworkred.domain.repository

import com.clockworkred.domain.model.TheoryNote
import com.clockworkred.domain.model.TheoryTopic

/** Repository providing theory notes for the helper feature. */
interface TheoryRepository {
    /** Fetch the note for the given topic. */
    suspend fun fetchNoteFor(topic: TheoryTopic): TheoryNote
}
