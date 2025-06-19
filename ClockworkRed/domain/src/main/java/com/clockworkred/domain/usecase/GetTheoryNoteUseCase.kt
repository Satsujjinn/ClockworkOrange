package com.clockworkred.domain.usecase

import com.clockworkred.domain.model.TheoryNote
import com.clockworkred.domain.model.TheoryTopic
import com.clockworkred.domain.repository.TheoryRepository
import javax.inject.Inject

/** Use case fetching a theory note for a given topic. */
class GetTheoryNoteUseCase @Inject constructor(
    private val theoryRepository: TheoryRepository
) {
    suspend operator fun invoke(topic: TheoryTopic): Result<TheoryNote> = runCatching {
        theoryRepository.fetchNoteFor(topic)
    }
}
