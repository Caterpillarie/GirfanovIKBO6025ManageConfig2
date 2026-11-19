package main.kotlin.data

import main.kotlin.domain.PackageRepository
import java.lang.IllegalArgumentException
import java.net.URI

/**
 * An [PackageRepository] implementation that downloads the package index via HTTP or HTTPS.
 *
 * @property repositoryUrl Alpine Linux repository URL.
 * @property extractor Unpacker for the APKINDEX.tar.gz.
 * @property parser APKINDEX contents' parser.
 */
class ApkNetworkRepository(
    private val repositoryUrl: String,
    private val extractor: TarGzExtractor = TarGzExtractor(),
    private val parser: ApkIndexParser = ApkIndexParser()
) : PackageRepository {

    private var cachedDependencies: Map<String, List<String>>? = null

    override fun validateSource() {
        if(cachedDependencies == null) {
            cachedDependencies = loadAndCacheIndex()
        }
    }

    override fun getDirectDependencies(packageName: String): List<String> {
        val dependenciesMap = cachedDependencies ?: loadAndCacheIndex()
        return dependenciesMap[packageName]
            ?: throw IllegalArgumentException("Package '$packageName' doesn't found")
    }

    private fun loadAndCacheIndex(): Map<String, List<String>> {
        val indexUrl = buildIndexUrl(repositoryUrl)
        val indexText = downloadAndExtractIndex(indexUrl)
        val parsedMap = parseIndexSafely(indexUrl, indexText)
        cachedDependencies = parsedMap
        return parsedMap
    }

    private fun downloadAndExtractIndex(indexUrl: String): String {
        return runCatching {
            URI(indexUrl).toURL().openStream().use { stream ->
                extractor.extractFileText(stream, INDEX_FILE_NAME)
            }
        }.getOrElse { error ->
            throw kotlin.IllegalArgumentException(
                "Failed to load the package index from the address '$indexUrl': ${error.message}."
            )
        }
    }

    private fun parseIndexSafely(indexUrl: String, indexText: String): Map<String, List<String>> {
        val parsed = parser.parseDependenciesMap(indexText)
        require(parsed.isNotEmpty()) {
            "The package index at the address '$indexUrl' is empty or has an invalid format."
        }
        return parsed
    }

    private fun buildIndexUrl(baseUrl: String): String {
        val trimmed = baseUrl.trimEnd('/')
        return if (trimmed.endsWith(ARCHIVE_FILE_NAME)) trimmed else "$trimmed/$ARCHIVE_FILE_NAME"
    }

    companion object {
        private const val ARCHIVE_FILE_NAME = "APKINDEX.tar.gz"
        private const val INDEX_FILE_NAME = "APKINDEX"
    }
}