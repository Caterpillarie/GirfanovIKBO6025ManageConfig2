package main.kotlin.domain

/**
 * A scenario for constructing a dependency graph using recursive DFS.
 *
 * @property repository Dependencies data source repository
 */
class BuildDependencyGraphUseCase(
    private val repository: PackageRepository
) {

    /**
     * Builds a dependency graph starting from the root package.
     *
     * @param rootPackage Name of the target package to analyze.
     * @param maxDepth Maximum dependency traversal depth.
     * @param isReverse
     * @return Root node of the constructed dependency graph.
     */
    operator fun invoke(
        rootPackage: String,
        maxDepth: Int,
        isReverse: Boolean = false
    ): DependencyNode {
        repository.validateSource()
        val directGraph = repository.getAllDependencies()
        val adjacencyMap = if (isReverse) invertGraph(directGraph) else directGraph
        require(rootPackage in adjacencyMap) {
            "Package '$rootPackage' not found in repository."
        }
        return buildNodeRecursive(
            packageName = rootPackage,
            currentDepth = ROOT_DEPTH,
            maxDepth = maxDepth,
            currentPath = setOf(rootPackage),
            adjacencyMap = adjacencyMap
        )
    }

    private fun invertGraph(
        directGraph: Map<String, List<String>>
    ): Map<String, List<String>> {
        val inverted = LinkedHashMap<String, MutableList<String>>()
        for (pkg in directGraph.keys) {
            inverted[pkg] = mutableListOf()
        }
        for ((pkg, dependencies) in directGraph) {
            for (dep in dependencies) {
                inverted.getOrPut(dep) { mutableListOf() }.add(pkg)
            }
        }
        return inverted
    }

    private fun buildNodeRecursive(
        packageName: String,
        currentDepth: Int,
        maxDepth: Int,
        currentPath: Set<String>,
        adjacencyMap: Map<String, List<String>>
    ): DependencyNode {
        if (currentDepth >= maxDepth) {
            return DependencyNode(name = packageName, depth = currentDepth)
        }
        val neighbors = adjacencyMap[packageName].orEmpty()
        val children = neighbors.map { depName ->
            createChildNode(
                depName = depName,
                nextDepth = currentDepth + DEPTH_STEP,
                maxDepth = maxDepth,
                currentPath = currentPath,
                adjacencyMap = adjacencyMap
            )
        }
        return DependencyNode(
            name = packageName,
            depth = currentDepth,
            dependencies = children
        )
    }

    private fun createChildNode(
        depName: String,
        nextDepth: Int,
        maxDepth: Int,
        currentPath: Set<String>,
        adjacencyMap: Map<String, List<String>>
    ): DependencyNode {
        if (depName in currentPath) {
            return DependencyNode(name = depName, depth = nextDepth, isCycle = true)
        }
        return buildNodeRecursive(
            packageName = depName,
            currentDepth = nextDepth,
            maxDepth = maxDepth,
            currentPath = currentPath + depName,
            adjacencyMap = adjacencyMap
        )
    }

    companion object {
        private const val ROOT_DEPTH = 0
        private const val DEPTH_STEP = 1
    }
}