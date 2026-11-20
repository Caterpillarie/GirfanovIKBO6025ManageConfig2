package main.kotlin.data

/**
 * Alpine Linux package text index parser.
 */
class ApkIndexParser {

    /**
     * Constructs a mapping: package name -> list of names of its direct dependencies.
     *
     * @param indexContent APKINDEX files' contents.
     * @return A map of direct dependencies for each package in the index.
     */
    fun parseDependenciesMap(indexContent: String): Map<String, List<String>> {
        val rawPackages = parseRawEntries(indexContent)
        val providers = buildProviderMap(rawPackages)
        return rawPackages.associate { entry ->
            val resolvedDeps = entry.rawDependencies
                .mapNotNull { dep -> resolveDependencyName(rawDep = dep, providers) }
                .filter { it != entry.name }
                .distinct()
            entry.name to resolvedDeps
        }
    }

    private fun parseRawEntries(content: String): List<RawApkEntry> {
        return content.split("\n\n")
            .filter { it.isNotBlank() }
            .mapNotNull { parseSingleBlock(it) }
    }

    private fun parseSingleBlock(block: String): RawApkEntry? {
        var name: String? = null
        var deps = emptyList<String>()
        var provides = emptyList<String>()

        for (line in block.lines()) {
            when {
                line.startsWith(PREFIX_NAME) -> name = line.removePrefix(PREFIX_NAME)
                line.startsWith(PREFIX_DEPS) -> deps = splitTokens(line, PREFIX_DEPS)
                line.startsWith(PREFIX_PROVIDES) -> provides = splitTokens(line, PREFIX_PROVIDES)
            }
        }
        return name?.let { RawApkEntry(name = it, rawDependencies = deps, provides = provides) }
    }

    private fun splitTokens(line: String, prefix: String): List<String> {
        return line.removePrefix(prefix)
            .trim()
            .split(' ')
            .filter { it.isNotBlank() }
    }

    private fun buildProviderMap(entries: List<RawApkEntry>): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (entry in entries) {
            map[entry.name] = entry.name
            for (provided in entry.provides) {
                val cleanName = stripVersionConstraint(provided)
                map.putIfAbsent(cleanName, entry.name)
            }
        }
        return map
    }

    private fun resolveDependencyName(rawDep: String, providers: Map<String, String>): String? {
        if (rawDep.startsWith(CONFLICT_PREFIX)) return null
        val cleanName = stripVersionConstraint(rawDep)
        return providers[cleanName] ?: cleanName
    }

    private fun stripVersionConstraint(token: String): String {
        return token.split('=', '<', '>', '~').first()
    }

    private data class RawApkEntry(
        val name: String,
        val rawDependencies: List<String>,
        val provides: List<String>
    )

    companion object {
        private const val PREFIX_NAME = "P:"
        private const val PREFIX_DEPS = "D:"
        private const val PREFIX_PROVIDES = "p:"
        private const val CONFLICT_PREFIX = "!"
    }
}