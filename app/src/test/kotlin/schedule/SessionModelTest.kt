package schedule


import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SessionModelTest {
    private val day = LocalDate.of(2026, 10, 10)

    @Test
    fun createsAllFourTypes() {
        Lecture(day, "Topic", "Room 1", listOf("Ivanov"))
        Practice(day, "Topic", "Room 1", listOf("Ivanov"))
        Exam(day, "Topic", "Room 1", listOf("Ivanov", "Petrov"))
        Cancelled(day, "Topic")
    }

    @Test
    fun teacherOrderIsPreserved() {
        val exam = Exam(day, "T", "R", listOf("Petrov", "Ivanov"))

        assertEquals(listOf("Petrov", "Ivanov"), exam.teachers)
    }

    @Test
    fun caseMattersForTeacherNames() {
        Exam(day, "T", "R", listOf("Ivanov", "ivanov"))
    }

    @Test
    fun blankTopicIsRejected() {
        assertFailsWith<IllegalArgumentException> { Lecture(day, "", "R", listOf("I")) }
        assertFailsWith<IllegalArgumentException> { Lecture(day, "   ", "R", listOf("I")) }
        assertFailsWith<IllegalArgumentException> { Cancelled(day, "") }
    }

    @Test
    fun yearOutOfRangeIsRejected() {
        assertFailsWith<IllegalArgumentException> { Cancelled(LocalDate.of(0, 1, 1), "T") }
        assertFailsWith<IllegalArgumentException> { Cancelled(LocalDate.of(10000, 1, 1), "T") }
    }

    @Test
    fun activeSessionWithoutTeachersIsRejected() {
        assertFailsWith<IllegalArgumentException> { Lecture(day, "T", "R", emptyList()) }
    }

    @Test
    fun examWithOneTeacherIsRejected() {
        assertFailsWith<IllegalArgumentException> { Exam(day, "T", "R", listOf("Ivanov")) }
    }

    @Test
    fun examWithDuplicateTeacherIsRejected() {
        assertFailsWith<IllegalArgumentException> { Exam(day, "T", "R", listOf("Ivanov", "Ivanov")) }
    }

    @Test
    fun badLocationIsRejected() {
        assertFailsWith<IllegalArgumentException> { Lecture(day, "T", "-", listOf("I")) }
        assertFailsWith<IllegalArgumentException> { Lecture(day, "T", " Room", listOf("I")) }
    }

    @Test
    fun controlCharactersAndWhitespaceAreRejected() {
        assertFailsWith<IllegalArgumentException> { Cancelled(day, "a\tb") }
        assertFailsWith<IllegalArgumentException> { Cancelled(day, " topic") }
    }

    @Test
    fun changingOriginalListDoesNotAffectOnSession () {
        val names = mutableListOf("Ivanov", "Petrov")
        val exam = Exam(day, "T", "R", names)

        names.clear()

        assertEquals(listOf("Ivanov", "Petrov"), exam.teachers)
    }

    @Test
    fun changingReturnedListDoesNotAffectOnSession () {
        val exam = Exam(day, "T", "R", listOf("Ivanov", "Petrov"))
        val taken = exam.teachers as MutableList<String>

        taken.clear()

        assertEquals(listOf("Ivanov", "Petrov"), exam.teachers)
    }
}
