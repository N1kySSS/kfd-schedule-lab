package schedule

fun sessionTypeOf(session: Session): SessionType {
    return when (session) {
        is Lecture -> SessionType.LECTURE
        is Practice -> SessionType.PRACTICE
        is Exam -> SessionType.EXAM
        is Cancelled -> SessionType.CANCELLED
    }
}

private fun locationOf(session: Session): String {
    return when (session) {
        is Lecture -> session.location
        is Practice -> session.location
        is Exam -> session.location
        is Cancelled -> "-"
    }
}

private fun teachersOf(session: Session): String {
    return when (session) {
        is Lecture -> session.teachers.joinToString(", ")
        is Practice -> session.teachers.joinToString(", ")
        is Exam -> session.teachers.joinToString(", ")
        is Cancelled -> "-"
    }
}

fun formatPeriodLine(session: Session): String {
    return "${session.date} | ${sessionTypeOf(session).name} | ${session.topic} | ${locationOf(session)}"
}

fun formatTypeLine(session: Session): String {
    return formatPeriodLine(session) + " | ${teachersOf(session)}"
}
