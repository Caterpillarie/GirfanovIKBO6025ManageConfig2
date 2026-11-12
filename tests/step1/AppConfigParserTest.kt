package step1

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import main.kotlin.presentation.AppConfigParser

/**
 * Модульные тесты для парсера командной строки.
 */
class AppConfigParserTest {
    private val parser = AppConfigParser()

    /**
     * Проверяет корректный разбор полного набора опций командной строки.
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
     * Проверяет ошибку при отсутствии обязательного параметра.
     */
    @Test
    fun repoIsMissing() {
        val args = arrayOf("--package", "git")

        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(args)
        }
    }

    /**
     * Проверяет ошибку при передаче нечислового значения глубины.
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
     * Проверяет ошибку при передаче неизвестного флага.
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