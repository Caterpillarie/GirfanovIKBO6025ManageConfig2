package step1

import main.kotlin.domain.AppConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows

class AppConfigTest {
    /**
     * Проверяет успешное создание конфигурации при корректных параметрах.
     */
    @Test
    fun createValidConfig() {
        val config = AppConfig(
            packageName = VALID_NOT_TEST_PACKAGE_NAME,
            repositoryPath = VALID_URL,
            isTest = false,
            isASCII = true,
            maxDepth = VALID_DEPTH
        )

        assertEquals(VALID_NOT_TEST_PACKAGE_NAME, config.packageName)
        assertEquals(VALID_DEPTH, config.maxDepth)
    }

    /**
     * Проверяет выброс исключения при пустом имени пакета.
     */
    @Test
    fun packageNameIsBlank() {
        assertThrows(IllegalArgumentException::class.java) {
            AppConfig(
                packageName =  "",
                repositoryPath = VALID_URL,
                isTest = false,
                isASCII = false,
                maxDepth = VALID_DEPTH
            )
        }
    }

    /**
     * Проверяет запрет на строчные буквы в имени пакета в тестовом режиме.
     */
    @Test
    fun testPackageIsNotUppercase() {
        assertThrows(IllegalArgumentException::class.java) {
            AppConfig(
                packageName = VALID_NOT_TEST_PACKAGE_NAME,
                repositoryPath = INVALID_REPO,
                isTest = true,
                isASCII = false,
                maxDepth = VALID_DEPTH
            )
        }
    }

    /**
     * Проверяет валидацию максимальной глубины анализа.
     */
    @Test
    fun maxDepthIsInvalid() {
        assertThrows(IllegalArgumentException::class.java) {
            AppConfig(
                packageName = VALID_NOT_TEST_PACKAGE_NAME,
                repositoryPath = VALID_URL,
                isTest = false,
                isASCII = true,
                maxDepth = INVALID_DEPTH
            )
        }
    }

    /**
     * Проверяет валидацию URL в режиме реального репозитория.
     */
    @Test
    fun invalidURL() {
        assertThrows(IllegalArgumentException::class.java) {
            AppConfig(
                packageName = VALID_NOT_TEST_PACKAGE_NAME,
                repositoryPath = INVALID_URL,
                isTest = false,
                isASCII = false,
                maxDepth = VALID_DEPTH
            )
        }
    }

    companion object {
        private const val VALID_DEPTH = 3
        private const val VALID_URL = "https://test.org"
        private const val VALID_NOT_TEST_PACKAGE_NAME = "real"
        private const val INVALID_REPO = "repo.txt"
        private const val INVALID_DEPTH = 0
        private const val INVALID_URL = "htt://invalid.com"
    }
}
