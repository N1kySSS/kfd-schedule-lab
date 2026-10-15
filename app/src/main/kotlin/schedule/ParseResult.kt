package schedule

sealed interface ParseResult {
    data class Success(val session: Session) : ParseResult
    data class Failure(val lineNumber: Long, val reason: ParseFailure) : ParseResult
}

sealed interface ParseFailure {
    data object InvalidSeparator : ParseFailure
    data class WrongFieldCount(val actual: Int) : ParseFailure
    data class EmptyField(val field: String) : ParseFailure
    data class InvalidDate(val raw: String) : ParseFailure
    data class UnknownType(val raw: String) : ParseFailure
    data class IncorrectTeachersList(val type: String, val actual: Int) : ParseFailure
    data class ControlCharacter(val field: String, val value: String) : ParseFailure
    data class OuterWhitespace(val field: String, val value: String) : ParseFailure
    data class CancelledField(val field: String, val value: String) : ParseFailure
    data class InvalidLocation(val value: String) : ParseFailure
    data class InvalidTeacher(val value: String) : ParseFailure
    data class DuplicateTeacher(val name: String) : ParseFailure
}
