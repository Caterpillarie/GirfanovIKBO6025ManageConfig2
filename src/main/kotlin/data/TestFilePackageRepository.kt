package main.kotlin.data

import main.kotlin.domain.PackageRepository
import java.io.File

/**
 * An implementation of [PackageRepository] that reads a dependency graph from a test file.
 *
 * @property filePath Path to the local graph description file.
 */
class TestFilePackageRepository(
    private val filePath: String
) : PackageRepository {

    private var cachedGraph: Map<String, List<String>>? = null

    override fun validateSource() {
        val file = File(filePath)
        require(file.exists()) { "Test file not found: '$filePath'." }
        require(file.isFile) { "Not a file: '$filePath'." }
        require(file.canRead()) { "Access denied: '$filePath'." }
        require(file.length() > EMPTY_FILE_SIZE) { "File is empty: '$filePath'." }
    }

    override fun getDirectDependencies(packageName: String): List<String> {
        val graph = cachedGraph ?: loadAndCacheGraph()
        return graph[packageName]
            ?: throw IllegalArgumentException(
                "Package '$packageName' not found in test repository."
            )
    }

    override fun getAllDependencies(): Map<String, List<String>> {
        return cachedGraph ?: loadAndCacheGraph()
    }

    private fun loadAndCacheGraph(): Map<String, List<String>> {
        validateSource()
        val lines = File(filePath).readLines().filter { it.isNotBlank() }
        val result = mutableMapOf<String, List<String>>()
        for (line in lines) {
            val (node, deps) = parseLine(line)
            result[node] = deps
        }
        return result.also { cachedGraph = it }
    }

    private fun parseLine(line: String): Pair<String, List<String>> {
        require(line.contains(DELIMITER)) {
            "Invalid line in text file (waited 'A: B C'): '$line'."
        }
        val node = line.substringBefore(DELIMITER).trim()
        require(node.matches(NODE_REGEX)) {
            "Package name of a test file must be in uppercase: '$node'."
        }
        val rawDeps = line.substringAfter(DELIMITER).trim()
        val deps = if (rawDeps.isEmpty()) emptyList() else splitDependencies(rawDeps)
        return node to deps
    }

    private fun splitDependencies(rawDeps: String): List<String> {
        val tokens = rawDeps.split(WHITESPACE_REGEX)
        tokens.forEach { token ->
            require(token.matches(NODE_REGEX)) {
                "Incorrect dependency name in test file: '$token'."
            }
        }
        return tokens
    }

    companion object {
        private const val EMPTY_FILE_SIZE = 0L
        private const val DELIMITER = ":"
        private val NODE_REGEX = Regex("^[A-Z]+$")
        private val WHITESPACE_REGEX = Regex("\\s+")
    }
}