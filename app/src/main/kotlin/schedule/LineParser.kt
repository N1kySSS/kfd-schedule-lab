package schedule

import java.time.DateTimeException
import java.time.LocalDate

private val FIELD_NAMES = listOf("date", "type", "topic", "location", "teachers")

fun parseLine(number: Long, line: String): ParseResult {
    if (!line.contains("|")) {
        return ParseResult.Failure(number, ParseFailure.InvalidSeparator)
    }

    val fields = line.split("|")

    if (fields.size != 5) {
        return ParseResult.Failure(number, ParseFailure.WrongFieldCount(fields.size))
    }

    fields.forEachIndexed { index, value ->
        if (value.isBlank()) {
            return ParseResult.Failure(number, ParseFailure.EmptyField(FIELD_NAMES[index]))
        }
    }

    val dateText = fields[0]
    val typeText = fields[1]
    val topic = fields[2]
    val location = fields[3]
    val teachersText = fields[4]

    val date = parseDateOrNull(dateText) ?: return ParseResult.Failure(number, ParseFailure.InvalidDate(dateText))

    val type = SessionType.entries.firstOrNull { it.name == typeText }
        ?: return ParseResult.Failure(number, ParseFailure.UnknownType(typeText))

    val teachers = teachersText.split(",")

    if (type == SessionType.EXAM) {
        var correct = 0

        teachers.forEach { t ->
            if (t.isNotBlank() && t != "-") correct++
        }

        if (correct < 2) {
            return ParseResult.Failure(number, ParseFailure.IncorrectTeachersList(typeText, correct))
        }
    }

    val values = mutableListOf("topic" to topic, "location" to location)

    teachers.forEach { t ->
        values.add("teachers" to t)
    }

    for ((field, value) in values) {
        if (hasControlChars(value)) {
            return ParseResult.Failure(number, ParseFailure.ControlCharacter(field, value))
        }
    }

    for ((field, value) in values) {
        if (hasWhitespace(value)) {
            return ParseResult.Failure(number, ParseFailure.OuterWhitespace(field, value))
        }
    }

    if (type == SessionType.CANCELLED) {
        if (location != "-") {
            return ParseResult.Failure(number, ParseFailure.CancelledField("location", location))
        }

        if (teachersText != "-") {
            return ParseResult.Failure(number, ParseFailure.CancelledField("teachers", teachersText))
        }
    } else {
        if (location == "-") {
            return ParseResult.Failure(number, ParseFailure.InvalidLocation(location))
        }

        teachers.forEach { t ->
            if (t.isBlank() || t == "-") {
                return ParseResult.Failure(number, ParseFailure.InvalidTeacher(t))
            }
        }

        teachers.forEachIndexed { index, teacher ->
            if (teachers.indexOf(teacher) != index) {
                return ParseResult.Failure(number, ParseFailure.DuplicateTeacher(teacher))
            }
        }
    }

    val session = when (type) {
        SessionType.LECTURE -> Lecture(date, topic, location, teachers)
        SessionType.PRACTICE -> Practice(date, topic, location, teachers)
        SessionType.EXAM -> Exam(date, topic, location, teachers)
        SessionType.CANCELLED -> Cancelled(date, topic)
    }

    return ParseResult.Success(session)
}

private fun parseDateOrNull(text: String): LocalDate? {
    if (!Regex("\\d{4}-\\d{2}-\\d{2}").matches(text)) return null

    val year = text.substring(0, 4).toInt()
    val month = text.substring(5, 7).toInt()
    val day = text.substring(8, 10).toInt()

    if (year !in 1..9999) return null

    return try {
        LocalDate.of(year, month, day)
    } catch (_: DateTimeException) {
        null
    }
}
