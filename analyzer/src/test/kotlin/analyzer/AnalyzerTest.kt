package analyzer

import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class AnalyzerTest {
    private val sample = File("../docs/repo-analyzer/sample")

    @Test
    fun countsByExtension() {
        val expected = listOf(
            ExtensionStat("kt", 2, 4),
            ExtensionStat("md", 1, 0),
        )

        assertEquals(expected, analyze(sample).byExtension)
    }

    @Test
    fun findsLongestFiles() {
        val expected = listOf(
            SourceFile("src/A.kt", "kt", 2),
            SourceFile("src/B.kt", "kt", 2),
            SourceFile("README.md", "md", 0),
        )

        assertEquals(expected, analyze(sample).longest)
    }

    @Test
    fun showsAllWhenFewerThanThree(@TempDir dir: File) {
        write(dir, "a.kt", "one\ntwo")

        assertEquals(1, analyze(dir).longest.size)
    }

    @Test
    fun emptyDirectoryGivesEmptyReports(@TempDir dir: File) {
        val report = analyze(dir)

        assertEquals(emptyList<ExtensionStat>(), report.byExtension)
        assertEquals(emptyList<SourceFile>(), report.longest)
    }

    @Test
    fun skipsServiceDirectories(@TempDir dir: File) {
        write(dir, ".git/A.kt", "x")
        write(dir, ".gradle/B.kt", "x")
        write(dir, "build/C.kt", "x")
        write(dir, "src/D.kt", "x")

        assertEquals(listOf("src/D.kt"), analyze(dir).longest.map { it.path })
    }

    @Test
    fun failsWhenPathIsMissing(@TempDir dir: File) {
        assertThrows(IllegalArgumentException::class.java) {
            analyze(File(dir, "missing"))
        }
    }

    @Test
    fun failsWhenPathIsFile(@TempDir dir: File) {
        write(dir, "a.kt", "x")

        assertThrows(IllegalArgumentException::class.java) {
            analyze(File(dir, "a.kt"))
        }
    }

    private fun write(root: File, path: String, text: String) {
        val file = File(root, path)

        file.parentFile.mkdirs()
        file.writeText(text)
    }

    private fun createSample(root: File) {
        write(root, "src/A.kt", "package sample\nclass A")
        write(root, "src/B.kt", "package sample\nclass B\n")
        write(root, "README.md", "")
        write(root, "notes.txt", "ignored")
    }
}
