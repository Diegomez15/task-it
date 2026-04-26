import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.task_it.domain.model.Task
import com.example.task_it.presentation.components.TaskItem
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import com.example.task_it.domain.model.TaskPriority


@RunWith(AndroidJUnit4::class)
class TaskItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createFakeTask(): Task {
        return Task(
            id = 1,
            title = "Comprar pan",
            description = "Ir al Mercadona",
            date = LocalDate.now(),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = false
        )
    }

    @Test
    fun taskItem_displaysTitleCorrectly() {
        val task = createFakeTask()

        composeTestRule.setContent {
            TaskItem(
                task = task,
                onToggleCompleted = {}
            )
        }

        composeTestRule
            .onNodeWithTag("taskTitle")
            .assertExists()
            .assertTextEquals("Comprar pan")
    }

    @Test
    fun taskItem_callsCallback_whenCheckClicked() {
        val task = createFakeTask()
        var clicked = false

        composeTestRule.setContent {
            TaskItem(
                task = task,
                onToggleCompleted = { clicked = true }
            )
        }

        composeTestRule
            .onNodeWithTag("taskCheck")
            .performClick()

        assert(clicked)
    }
}