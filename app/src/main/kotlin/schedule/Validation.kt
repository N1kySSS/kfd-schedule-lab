package schedule

import java.time.LocalDate

fun hasControlChars(s: String): Boolean = s.any { it.isISOControl() }

fun hasWhitespace(s: String): Boolean =
    s.isNotEmpty() && (s.first().isWhitespace() || s.last().isWhitespace())

fun requireDate(date: LocalDate) {
    require(date.year in 1..9999) { "year must be in 1..9999, got ${date.year}" }
}

fun requireText(name: String, value: String) {
    require(value.isNotBlank()) { "$name must not be blank" }
    require(!hasControlChars(value)) { "$name must not contain control characters" }
    require(!hasWhitespace(value)) { "$name must not have leading or trailing whitespace" }
}

fun requireLocation(value: String) {
    requireText("location", value)
    require(value != "-") { "location must not be \"-\"" }
}

fun requireTeachers(teachers: List<String>, min: Int) {
    require(teachers.size >= min) { "at least $min teacher(s) required, got ${teachers.size}" }

    for (t in teachers) {
        requireText("teacher", t)
        require(t != "-") { "teacher must not be \"-\"" }
    }

    require(teachers.toSet().size == teachers.size) { "teachers must not repeat" }
}
