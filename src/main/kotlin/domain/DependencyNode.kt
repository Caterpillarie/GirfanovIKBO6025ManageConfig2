package main.kotlin.domain

/**
 * A node in the dependency tree of a package.
 *
 * @property name Package name.
 * @property depth A node in the dependency tree of a package.
 * @property isCycle An indication that further traversal has been halted due to a cycle.
 * @property dependencies List of dependency child nodes.
 */
data class DependencyNode(
    val name: String,
    val depth: Int,
    val isCycle: Boolean = false,
    val dependencies: List<DependencyNode> = emptyList()
)