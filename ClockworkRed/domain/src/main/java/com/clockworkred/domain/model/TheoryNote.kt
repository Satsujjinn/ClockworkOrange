package com.clockworkred.domain.model

/** Detailed explanation for a theory topic. */
data class TheoryNote(
    val title: String,
    val description: String,
    val examples: List<String>
)
