package main.kotlin.presentation

import main.kotlin.domain.DependencyNode

/**
 * Formats and outputs the dependency graph to the console
 */
class GraphPrinter {

    /**
     * Outputs the dependency graph tree to the console
     *
     * @param root Root node of the dependency graph.
     */
    fun print(root: DependencyNode, isReverse: Boolean = false) {
        println("Dependency graph for a package '${root.name}':")
        printNode(root)
    }

    private fun printNode(node: DependencyNode) {
        val indent = INDENT_UNIT.repeat(node.depth)
        val cycleMark = if (node.isCycle) CYCLE_SUFFIX else ""
        println("$indent- ${node.name+cycleMark}")
        node.dependencies.forEach { child -> printNode(child) }
    }

    companion object {
        private const val INDENT_UNIT = "  "
        private const val CYCLE_SUFFIX = " [cycle]"
    }
}