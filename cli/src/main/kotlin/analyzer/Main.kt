package analyzer

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: ./gradlew :analyzer:run --args=\"path-to-directory\" --console=plain -q")
        exitProcess(2)
    }

    val report = try {
        analyze(File(args[0]))
    } catch (e: IllegalArgumentException) {
        System.err.println("Error: ${e.message}")
        exitProcess(1)
    }

    printReport(report)
}

private fun printReport(report: Report) {
    println("By extension:")

    for (stat in report.byExtension) {
        println("${stat.extension}: ${stat.files} file, ${stat.lines} line")
    }

    println()
    println("Longest files:")

    for (file in report.longest) {
        println("${file.path} (${file.lines})")
    }
}
