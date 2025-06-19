package com.clockworkred.domain.model

/** Domain representation of a project. */
data class ProjectModel(
    val id: String,
    val name: String,
    val createdAt: Long
)
