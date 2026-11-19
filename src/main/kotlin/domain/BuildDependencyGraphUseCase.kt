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
     */
    operator fun invoke(rootPackage: String, maxDepth: Int): DependencyNode {
        repository.validateSource()
        repository.getDirectDependencies(rootPackage)
        return buildNodeRecursive(
            packageName = rootPackage,
            currentDepth = ROOT_DEPTH,
            maxDepth = maxDepth,
            currentPath = setOf(rootPackage)
        )
    }

    private fun buildNodeRecursive(
        packageName: String,
        currentDepth: Int,
        maxDepth: Int,
        currentPath: Set<String>
    ): DependencyNode {
        if (currentDepth >= maxDepth) {
            return DependencyNode(name = packageName, depth = currentDepth)
        }
        val directDeps = repository.getDirectDependencies(packageName)
        val children = directDeps.map { depName ->
            createChildNode(depName, currentDepth + DEPTH_STEP, maxDepth, currentPath)
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
        currentPath: Set<String>
    ): DependencyNode {
        if (depName in currentPath) {
            return DependencyNode(name = depName, depth = nextDepth, isCycle = true)
        }
        return buildNodeRecursive(
            packageName = depName,
            currentDepth = nextDepth,
            maxDepth = maxDepth,
            currentPath = currentPath + depName
        )
    }

    companion object {
        private const val ROOT_DEPTH = 0
        private const val DEPTH_STEP = 1
    }
}