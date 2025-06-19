package com.clockworkred.domain

import com.clockworkred.domain.model.StyleProfile
import kotlinx.coroutines.flow.Flow

/** Repository for providing style profiles. */
interface StyleRepository {
    fun getAllStyles(): Flow<List<StyleProfile>>
    fun getStyle(id: String): Flow<StyleProfile>
}
