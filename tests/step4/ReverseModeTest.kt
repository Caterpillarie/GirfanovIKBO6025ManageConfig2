package step4

import main.kotlin.domain.DependencyNode
import main.kotlin.presentation.AppConfigParser
import main.kotlin.presentation.GraphPrinter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Unit tests for reverse mode CLI parsing.
 */
class ReverseModeTest {

    private val parser = AppConfigParser()
    private val printer = GraphPrinter()

    /**
     * Verifies that --reverse key is parsed
     * into [main.kotlin.domain.AppConfig.isReverse].
     */
    @Test
    fun reverseKeyParsing() {
        val enabledConfig = parser.parse(
            arrayOf(
                "--package", "D",
                "--repo", "test.txt",
                "--reverse", "true",
                "--test", "true")
        )
        val defaultConfig = parser.parse(
            arrayOf(
                "--package", "D",
                "--repo", "test.txt",
                "--test", "true")
        )

        assertTrue(enabledConfig.isReverse)
        assertFalse(defaultConfig.isReverse)
    }

    /**
     * Verifies that invalid boolean value
     * or --reverse throws an exception.
     */
    @Test
    fun invalidReverse() {
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(
                arrayOf(
                    "--package", "D",
                    "--repo", "test.txt",
                    "--test", "true",
                    "--reverse", "invalid"
                )
            )
        }
    }

    /**
     * Verifies reverse dependency header and
     * indented list formatting with cycle marker.
     */
    @Test
    fun printReverseDependency() {
        val cycleB = DependencyNode(
            name = "B",
            depth = DEPTH_TWO,
            isCycle = true
        )
        val nodeA = DependencyNode(
            name = "A",
            depth = DEPTH_ONE,
            dependencies = listOf(cycleB)
        )
        val rootD = DependencyNode(
            name = "D",
            depth = DEPTH_ZERO,
            dependencies = listOf(nodeA)
        )

        val outputLines = captureStdout {
            printer.print(rootD)
        }

        val expectedLines = listOf(
            "Dependency graph for a package 'D':",
            "- D",
            "  - A",
            "    - B [cycle]"
        )
        assertEquals(expectedLines, outputLines)
    }

    private fun captureStdout(block: () -> Unit): List<String> {
        val originalOut = System.out
        val buffer = ByteArrayOutputStream()
        System.setOut(
            PrintStream(
                buffer, true, Charsets.UTF_8
            )
        )
        try {
            block()
        } finally {
            System.setOut(originalOut)
        }
        return buffer.toString(Charsets.UTF_8).lines().filter {
            it.isNotEmpty()
        }
    }

    companion object {
        private const val DEPTH_ZERO = 0
        private const val DEPTH_ONE = 1
        private const val DEPTH_TWO = 2
    }
}