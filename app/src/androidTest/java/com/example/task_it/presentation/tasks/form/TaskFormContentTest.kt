package com.example.task_it.presentation.tasks.form

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.task_it.domain.model.TaskPriority
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick

class TaskFormContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun titleField_isDisplayed() {
        composeTestRule.setContent {
            TaskFormContent(
                state = TaskFormUiState(),
                isEditMode = false,
                onTitleChange = {},
                onDescriptionChange = {},
                onPriorityChange = {},
                onDateClick = {},
                onTimeClick = {},
                onLocationChange = {},
                onCancel = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithTag("titleField")
            .assertIsDisplayed()
    }

    @Test
    fun whenTitleIsEmpty_submitButtonIsDisabled() {
        composeTestRule.setContent {
            TaskFormContent(
                state = TaskFormUiState(
                    title = ""
                ),
                isEditMode = false,
                onTitleChange = {},
                onDescriptionChange = {},
                onPriorityChange = {},
                onDateClick = {},
                onTimeClick = {},
                onLocationChange = {},
                onCancel = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithTag("submitButton")
            .assertIsNotEnabled()
    }

    @Test
    fun whenTitleIsValid_submitButtonIsEnabled() {
        composeTestRule.setContent {
            TaskFormContent(
                state = TaskFormUiState(
                    title = "Comprar pan",
                    isSubmitEnabled = true
                ),
                isEditMode = false,
                onTitleChange = {},
                onDescriptionChange = {},
                onPriorityChange = {},
                onDateClick = {},
                onTimeClick = {},
                onLocationChange = {},
                onCancel = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithTag("submitButton")
            .assertIsEnabled()
    }

    @Test
    fun whenUserTypesTitle_onTitleChangeIsCalled() {
        var writtenTitle = ""

        composeTestRule.setContent {
            TaskFormContent(
                state = TaskFormUiState(),
                isEditMode = false,
                onTitleChange = { writtenTitle = it },
                onDescriptionChange = {},
                onPriorityChange = {},
                onDateClick = {},
                onTimeClick = {},
                onLocationChange = {},
                onCancel = {},
                onSubmit = {}
            )
        }

        composeTestRule
            .onNodeWithTag("titleField")
            .performTextInput("Comprar pan")

        assert(writtenTitle == "Comprar pan")
    }

    @Test
    fun whenSubmitButtonClicked_onSubmitIsCalled() {
        var submitted = false

        composeTestRule.setContent {
            TaskFormContent(
                state = TaskFormUiState(
                    title = "Comprar pan",
                    isSubmitEnabled = true
                ),
                isEditMode = false,
                onTitleChange = {},
                onDescriptionChange = {},
                onPriorityChange = {},
                onDateClick = {},
                onTimeClick = {},
                onLocationChange = {},
                onCancel = {},
                onSubmit = { submitted = true }
            )
        }

        composeTestRule
            .onNodeWithTag("submitButton")
            .performClick()

        assert(submitted)
    }

}

