package com.example.task_it.presentation.tasks.form

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import java.time.LocalDate
import java.time.LocalTime

class TaskFormViewModelTest {

    private fun createViewModel(): TaskFormViewModel {
        val application = ApplicationProvider.getApplicationContext<Application>()

        return TaskFormViewModel(
            application = application,
            taskId = null
        )
    }

    @Test
    fun whenTitleIsEmpty_submitIsDisabled() {
        val viewModel = createViewModel()

        viewModel.onTitleChange("")

        val state = viewModel.uiState.value

        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun whenTitleIsValid_submitIsEnabled() {
        val viewModel = createViewModel()

        viewModel.onTitleChange("Comprar pan")

        val state = viewModel.uiState.value

        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun whenTitleExceedsMaxLength_titleIsTrimmedToMaxLength() {
        val viewModel = createViewModel()

        val longTitle = "A".repeat(TaskFormLimits.TITLE_MAX + 10)

        viewModel.onTitleChange(longTitle)

        val state = viewModel.uiState.value

        assertEquals(TaskFormLimits.TITLE_MAX, state.title.length)
    }

    @Test
    fun whenTitleChanges_submitStateUpdatesCorrectly() {
        val viewModel = createViewModel()

        // Inicialmente vacío → deshabilitado
        var state = viewModel.uiState.value
        assertFalse(state.isSubmitEnabled)

        // Usuario escribe título válido
        viewModel.onTitleChange("Comprar pan")

        state = viewModel.uiState.value

        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun whenDescriptionExceedsMaxLength_descriptionIsTrimmedToMaxLength() {
        val viewModel = createViewModel()

        val longDescription = "A".repeat(TaskFormLimits.DESCRIPTION_MAX + 10)

        viewModel.onDescriptionChange(longDescription)

        val state = viewModel.uiState.value

        assertEquals(TaskFormLimits.DESCRIPTION_MAX, state.description.length)
    }

    @Test
    fun whenDateIsInPast_dateTimeErrorIsShownAndSubmitIsDisabled() {
        val viewModel = createViewModel()

        val pastDate = LocalDate.now().minusDays(1)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDateChange(pastDate)

        val state = viewModel.uiState.value

        assertEquals("No puedes seleccionar una fecha pasada", state.dateTimeError)
        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun whenTodayTimeIsInPast_dateTimeErrorIsShownAndSubmitIsDisabled() {
        val viewModel = createViewModel()

        val today = LocalDate.now()
        val pastTime = LocalTime.now().minusHours(1)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDateChange(today)
        viewModel.onTimeChange(pastTime)

        val state = viewModel.uiState.value

        assertEquals("No puedes seleccionar una fecha y hora pasadas", state.dateTimeError)
        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun whenDateIsInFuture_dateTimeErrorIsNullAndSubmitIsEnabled() {
        val viewModel = createViewModel()

        val futureDate = LocalDate.now().plusDays(1)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDateChange(futureDate)

        val state = viewModel.uiState.value

        assertEquals(null, state.dateTimeError)
        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun whenLocationExceedsMaxLength_locationIsTrimmedToMaxLength() {
        val viewModel = createViewModel()

        val longLocation = "A".repeat(TaskFormLimits.LOCATION_MAX + 10)

        viewModel.onLocationChange(longLocation)

        val state = viewModel.uiState.value

        assertEquals(TaskFormLimits.LOCATION_MAX, state.location?.length)
    }
}