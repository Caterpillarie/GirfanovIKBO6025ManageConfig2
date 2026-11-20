package main.kotlin.domain

import java.net.URI

/**
 * Application configuration containing options for dependency analysis.
 *
 * @property packageName Name of analyzed package.
 * @property repositoryPath Repository's URL or path to test file.
 * @property isTest Enables test mode.
 * @property isASCII Enables ASCII tree format output.
 * @property maxDepth Max depth of dependency analysis.
 */
data class AppConfig(
    val packageName: String,
    val repositoryPath: String,
    val isTest: Boolean,
    val isASCII: Boolean,
    val isReverse: Boolean = false,
    val maxDepth: Int,
) {
    init {
        validatePackageName()
        validateRepositoryPath()
        validateMaxDepth()
    }

    private fun validatePackageName() {
        require(packageName.isNotBlank()) {
            "Package name can't be blank."
        }
        val pattern = if (isTest) TEST_PACK_REG else PACK_REG
        require(packageName.matches(pattern)) {
            "Package name '$packageName' is invalid."
        }
    }

    private fun validateRepositoryPath() {
        require(repositoryPath.isNotBlank()) {
            "Repository path or URL can't be blank."
        }
        if (!isTest) {
            validateUrl(repositoryPath)
        }
    }

    private fun validateUrl(url: String) {
        val uri = runCatching { URI(url) }.getOrNull()
        val isValid = uri?.scheme == "http" || uri?.scheme == "https"
        require(uri != null && isValid && !uri.host.isNullOrBlank()) {
            "Invalid repository URL: '$url'."
        }
    }

    private fun validateMaxDepth() {
        require(maxDepth >= MIN_DEPTH) {
            "Max depth can't be lower than $MIN_DEPTH."
        }
    }

    companion object {
        private const val MIN_DEPTH = 1
        private val TEST_PACK_REG = Regex("^[A-Z]+$")
        private val PACK_REG = Regex("^[a-zA-Z0-9._+-]+$")
    }
}