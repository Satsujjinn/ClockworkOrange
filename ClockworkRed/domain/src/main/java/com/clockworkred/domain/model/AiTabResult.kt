package com.clockworkred.domain.model

/** Result returned by the AI tab generation service. */
data class AiTabResult(
    val tabText: String,
    val theoryNotes: String?
)
