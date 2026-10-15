package schedule

interface CommandHandlers {
    fun period(command: Command.Period): Int
    fun byType(command: Command.ByType): Int
    fun errors(command: Command.Errors): Int
}

object StudentCommandHandlers : CommandHandlers {
    override fun period(command: Command.Period): Int = runPeriod(command)
    override fun byType(command: Command.ByType): Int = runType(command)
    override fun errors(command: Command.Errors): Int = runErrors(command)
}

fun runPeriod(command: Command.Period): Int {
    var total = 0
    var rejected = 0

    forEachScheduleLine(command.file) { number, line ->
        val result = parseLine(number, line)

        if (result is ParseResult.Success) {
            val date = result.session.date

            if (date in command.from..command.to) {
                println(formatPeriodLine(result.session))
                total++
            }
        } else {
            rejected++
        }
    }

    println("Total: $total")

    return if (rejected > 0) 1 else 0
}

fun runType(command: Command.ByType): Int {
    var total = 0
    var rejected = 0

    forEachScheduleLine(command.file) { number, line ->
        val result = parseLine(number, line)

        if (result is ParseResult.Success) {
            if (sessionTypeOf(result.session) == command.type) {
                println(formatTypeLine(result.session))
                total++
            }
        } else {
            rejected++
        }
    }

    println("Total: $total")

    return if (rejected > 0) 1 else 0
}

fun runErrors(command: Command.Errors): Int {
    var lines = 0
    var rejected = 0

    forEachScheduleLine(command.file) { number, line ->
        lines++

        val result = parseLine(number, line)

        if (result is ParseResult.Failure) {
            println(formatFailure(result))
            rejected++
        }
    }

    println("Rejected: $rejected of $lines")

    return if (rejected > 0) 1 else 0
}
