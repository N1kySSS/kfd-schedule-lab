package schedule

fun formatFailure(failure: ParseResult.Failure): String {
    val text = when (val reason = failure.reason) {
        ParseFailure.InvalidSeparator -> "invalid field separator"
        is ParseFailure.WrongFieldCount -> "got ${reason.actual} fields, expected 5"
        is ParseFailure.EmptyField -> "empty required field ${safeOutput(reason.field)}"
        is ParseFailure.InvalidDate -> "invalid date ${safeOutput(reason.raw)}"
        is ParseFailure.UnknownType -> "unknown session type ${safeOutput(reason.raw)}"
        is ParseFailure.IncorrectTeachersList ->
            "type ${safeOutput(reason.type)} requires at least 2 teachers, got ${reason.actual}"
        is ParseFailure.ControlCharacter ->
            "field ${safeOutput(reason.field)} contains a control character in ${safeOutput(reason.value)}"
        is ParseFailure.OuterWhitespace ->
            "field ${safeOutput(reason.field)} has leading or trailing whitespace in ${safeOutput(reason.value)}"
        is ParseFailure.CancelledField ->
            "cancelled session requires \"-\" in field ${safeOutput(reason.field)}, got ${safeOutput(reason.value)}"
        is ParseFailure.InvalidLocation -> "invalid location ${safeOutput(reason.value)}"
        is ParseFailure.InvalidTeacher -> "invalid teacher name ${safeOutput(reason.value)}"
        is ParseFailure.DuplicateTeacher -> "duplicate teacher ${safeOutput(reason.name)}"
    }

    return "line ${failure.lineNumber}: $text"
}

private fun safeOutput(value: String): String {
    val result = StringBuilder("\"")

    for (c in value) {
        if (c == '\\') {
            result.append("\\\\")
        } else if (c == '"') {
            result.append("\\\"")
        } else if (c.code in 32..126) {
            result.append(c)
        } else {
            result.append("\\u").append(String.format("%04X", c.code))
        }
    }

    result.append("\"")

    return result.toString()
}
