package step1

import main.kotlin.domain.AppConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows

/**
 * Unit tests for the configuration class.
 */
class AppConfigTest {
    /**
     * Verifies that the configuration is successfully
     * created when valid parameters are provided.
     */
    @Test
    fun createValidConfig() {
        val config = AppConfig(
            packageName = VALID_NOT_TEST_PACKAGE_NAME,
            repositoryPath = VALID_URL,
            isTest = false,
            isASCII = true,
            maxDepth = VALID_DEPTH,
        )

        assertEquals(VALID_NOT_TEST_PACKAGE_NAME, config.packageName)
        assertEquals(VALID_DEPTH, config.maxDepth)
    }

    /**
     * Verifies that an exception is thrown when the package name is empty.
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
     * Checks for the prohibition of lowercase letters in a package name, in test mode.
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
     * Verifies the validation of the maximum analysis depth.
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
     * Checks URL validation in the context of an actual repository.
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
