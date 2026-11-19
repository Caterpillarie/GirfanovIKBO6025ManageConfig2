package step1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import main.kotlin.presentation.AppConfigParser

/**
 * Unit tests for the command-line arguments parser.
 */
class AppConfigParserTest {
    private val parser = AppConfigParser()

    /**
     * Verifies the correct parsing of the full set of command-line options.
     */
    @Test
    fun validCase() {
        val args = arrayOf(
            "--package", "git",
            "--repo", "https://example.com/repo",
            "--test", "false",
            "--ascii", "true",
            "--depth", "5"
        )

        val config = parser.parse(args)

        assertEquals("git", config.packageName)
        assertFalse(config.isTest)
        assertTrue(config.isASCII)
        assertEquals(EXPECTED_DEPTH, config.maxDepth)
    }

    /**
     * Checks for an error when a required parameter is missing.
     */
    @Test
    fun repoIsMissing() {
        val args = arrayOf("--package", "git")

        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(args)
        }
    }

    /**
     * Checks for an error when a non-numeric depth value is passed.
     */
    @Test
    fun depthIsNotInt() {
        val args = arrayOf(
            "--package", "git",
            "--repo", "https://example.com/repo",
            "--depth", "abc"
        )

        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(args)
        }
    }

    /**
     * Checks for an error when an unknown key is passed.
     */
    @Test
    fun keyIsUnknown() {
        val args = arrayOf(
            "--package", "git",
            "--repo", "https://example.com/repo",
            "--unknown", "value"
        )

        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(args)
        }
    }

    companion object {
        private const val EXPECTED_DEPTH = 5
    }

}