package schedule

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ParserTest {
    private fun reasonOf(line: String): ParseFailure {
        val result = parseLine(1, line)

        assertIs<ParseResult.Failure>(result)

        return result.reason
    }

    @Test
    fun parsesValidLines() {
        val lecture = parseLine(1, "2026-10-05|LECTURE|Classes and delegation|Room 312|Portnov")

        assertIs<ParseResult.Success>(lecture)
        assertIs<Lecture>(lecture.session)

        val cancelled = parseLine(2, "2026-10-19|CANCELLED|Lecture postponed|-|-")

        assertIs<ParseResult.Success>(cancelled)
        assertIs<Cancelled>(cancelled.session)
    }

    @Test
    fun invalidSeparator() {
        val result = parseLine(7, "no separators")

        assertIs<ParseResult.Failure>(result)

        assertEquals(7L, result.lineNumber)
        assertEquals(ParseFailure.InvalidSeparator, result.reason)
    }

    @Test
    fun wrongFieldCount() {
        assertEquals(ParseFailure.WrongFieldCount(6), reasonOf("a|b|c|d|e|f"))
    }

    @Test
    fun emptyField() {
        assertEquals(ParseFailure.EmptyField("topic"), reasonOf("2026-10-05|LECTURE|   |Room|Ivanov"))
    }

    @Test
    fun trailingEmptyField() {
        assertEquals(ParseFailure.EmptyField("teachers"), reasonOf("2026-10-05|LECTURE|Topic|Room|"))
    }

    @Test
    fun invalidDateBeatsUnknownType() {
        assertEquals(
            ParseFailure.InvalidDate("2026-13-45"),
            reasonOf("2026-13-45|SEMINAR|Topic|Room 1|Ivanov")
        )
    }

    @Test
    fun unknownType() {
        assertEquals(ParseFailure.UnknownType("lecture"), reasonOf("2026-10-05|lecture|Topic|Room|Ivanov"))
    }

    @Test
    fun examWithOneTeacher() {
        assertEquals(
            ParseFailure.IncorrectTeachersList("EXAM", 1),
            reasonOf("2026-10-05|EXAM|Topic|Room|Ivanov")
        )
    }

    @Test
    fun controlCharacterInTopic() {
        assertEquals(
            ParseFailure.ControlCharacter("topic", "a\tb"),
            reasonOf("2026-10-05|LECTURE|a\tb|Room|Ivanov")
        )
    }

    @Test
    fun controlCharacterBeatsWhitespaceInEarlierField() {
        assertEquals(
            ParseFailure.ControlCharacter("location", "Room\t"),
            reasonOf("2026-10-05|LECTURE| Topic|Room\t|Ivanov")
        )
    }

    @Test
    fun outerWhitespaceInTopic() {
        assertEquals(
            ParseFailure.OuterWhitespace("topic", " Topic"),
            reasonOf("2026-10-05|LECTURE| Topic|Room|Ivanov")
        )
    }

    @Test
    fun cancelledWithLocation() {
        assertEquals(
            ParseFailure.CancelledField("location", "Room"),
            reasonOf("2026-10-05|CANCELLED|Topic|Room|-")
        )
    }

    @Test
    fun lectureWithDashLocation() {
        assertEquals(ParseFailure.InvalidLocation("-"), reasonOf("2026-10-05|LECTURE|Topic|-|Ivanov"))
    }

    @Test
    fun lectureWithDashTeacher() {
        assertEquals(ParseFailure.InvalidTeacher("-"), reasonOf("2026-10-05|LECTURE|Topic|Room|-"))
    }

    @Test
    fun examWithDuplicateTeachers() {
        assertEquals(
            ParseFailure.DuplicateTeacher("Ivanov"),
            reasonOf("2026-10-05|EXAM|Topic|Room 1|Ivanov,Ivanov")
        )
    }
}
