package schedule

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FormatterTest {
    private fun format(reason: ParseFailure, lineNumber: Long = 3): String {
        return formatFailure(ParseResult.Failure(lineNumber, reason))
    }

    @Test
    fun requiredMessages() {
        assertEquals("line 3: invalid date \"2026-13-45\"", format(ParseFailure.InvalidDate("2026-13-45")))
        assertEquals("line 3: empty required field \"date\"", format(ParseFailure.EmptyField("date")))
        assertEquals("line 3: invalid field separator", format(ParseFailure.InvalidSeparator))
        assertEquals("line 3: unknown session type \"SEMINAR\"", format(ParseFailure.UnknownType("SEMINAR")))

        assertEquals(
            "line 3: type \"EXAM\" requires at least 2 teachers, got 1",
            format(ParseFailure.IncorrectTeachersList("EXAM", 1))
        )

        assertEquals("line 3: got 6 fields, expected 5", format(ParseFailure.WrongFieldCount(6)))
    }

    @Test
    fun escapesSpecialCharacters() {
        assertEquals(
            "line 1: unknown session type \"a\\\\b\\\"c\"",
            format(ParseFailure.UnknownType("a\\b\"c"), 1)
        )

        assertEquals("line 1: unknown session type \"\\u000A\"", format(ParseFailure.UnknownType("\n"), 1))

        assertEquals("line 1: unknown session type \"\\u0416\"", format(ParseFailure.UnknownType("Ж"), 1))
    }
}
