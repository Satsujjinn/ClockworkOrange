package com.clockworkred.app.arrangement

import com.clockworkred.domain.ArrangementRepository
import com.clockworkred.domain.model.ArrangementStructure
import com.clockworkred.domain.model.SongSection
import com.clockworkred.domain.model.SongSectionOrder
import com.clockworkred.domain.usecase.GetArrangementStructureUseCase
import com.clockworkred.domain.usecase.SaveArrangementUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

private class FakeArrangementRepository : ArrangementRepository {
    val flow = MutableStateFlow(
        ArrangementStructure(
            listOf(
                SongSectionOrder(SongSection.INTRO, 0),
                SongSectionOrder(SongSection.VERSE, 1)
            )
        )
    )

    override fun getArrangement(): Flow<ArrangementStructure> = flow.asStateFlow()
    override suspend fun saveArrangement(structure: ArrangementStructure) { flow.value = structure }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ArrangementViewModelTest {
    private lateinit var viewModel: ArrangementViewModel
    private lateinit var repo: FakeArrangementRepository

    @Before
    fun setup() {
        repo = FakeArrangementRepository()
        val getUseCase = GetArrangementStructureUseCase(repo)
        val saveUseCase = SaveArrangementUseCase(repo)
        viewModel = ArrangementViewModel(getUseCase, saveUseCase)
    }

    @Test
    fun initialState_loadsDefaultArrangement() = runTest {
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.structure.sections.size)
    }

    @Test
    fun moveSection_updatesOrder() = runTest {
        advanceUntilIdle()
        viewModel.moveSection(0, 1)
        val second = viewModel.uiState.value.structure.sections[1]
        assertEquals(SongSection.INTRO, second.section)
    }

    @Test
    fun saveArrangement_togglesFlag() = runTest {
        advanceUntilIdle()
        viewModel.saveArrangement()
        assertFalse(viewModel.uiState.value.isSaving)
    }
}
