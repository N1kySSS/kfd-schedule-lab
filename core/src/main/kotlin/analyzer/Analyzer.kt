package analyzer

import java.io.File
import java.nio.file.Files

data class SourceFile(val path: String, val extension: String, val lines: Int)

data class ExtensionStat(val extension: String, val files: Int, val lines: Int)

data class Report(val byExtension: List<ExtensionStat>, val longest: List<SourceFile>)

private val extensions = setOf("kt", "kts", "md")
private val skippedDirs = setOf(".git", ".gradle", "build")

fun analyze(root: File): Report {
    require(root.isDirectory) { "Path does not exist or is not a directory: ${root.path}" }

    val files = collectFiles(root)

    return Report(countByExtension(files), findLongest(files))
}

private fun collectFiles(root: File): List<SourceFile> {
    val result = mutableListOf<SourceFile>()

    val walk = root.walkTopDown().onEnter { dir ->
        dir == root || (dir.name !in skippedDirs && !Files.isSymbolicLink(dir.toPath()))
    }

    for (file in walk) {
        if (!file.isFile) continue

        if (Files.isSymbolicLink(file.toPath())) continue

        if (file.extension !in extensions) continue

        val lines = file.useLines { it.count() }
        val path = file.relativeTo(root).invariantSeparatorsPath

        result.add(SourceFile(path, file.extension, lines))
    }

    return result
}

private fun countByExtension(files: List<SourceFile>): List<ExtensionStat> {
    val groups = files.groupBy { it.extension }
    val result = mutableListOf<ExtensionStat>()

    for (extension in groups.keys.sorted()) {
        val group = groups.getValue(extension)

        var totalLines = 0

        for (file in group) {
            totalLines += file.lines
        }

        result.add(ExtensionStat(extension, group.size, totalLines))
    }

    return result
}

private fun findLongest(files: List<SourceFile>): List<SourceFile> {
    val sorted = files.sortedWith(
        compareByDescending<SourceFile> { it.lines }.thenBy { it.path }
    )

    return sorted.take(3)
}
