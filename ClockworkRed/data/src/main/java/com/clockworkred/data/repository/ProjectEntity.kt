package com.clockworkred.data.repository

/** Data layer representation of a project. */
data class ProjectEntity(
    val id: String,
    val name: String,
    val createdAt: Long
)
