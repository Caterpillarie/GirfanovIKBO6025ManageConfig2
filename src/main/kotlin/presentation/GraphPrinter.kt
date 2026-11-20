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
    fun print(
        root: DependencyNode,
        isASCII: Boolean = false,
        isReverse: Boolean = false
    ) {
        println("Dependency graph for a package '${root.name}':")
        if(isASCII) {
            printASCII(root)
        } else {
            printNode(root)
        }
    }

    private fun printASCII(root: DependencyNode) {
        println(root.name)
        val children = root.dependencies
        children.forEachIndexed { index, node ->
            val isLast = index == children.lastIndex
            printASCIIChild(node, prefix = "", isLast = isLast)
        }
    }

    private fun printASCIIChild(
        node: DependencyNode,
        prefix: String,
        isLast: Boolean
    ) {
        val connector = if (isLast) LAST_BRANCH else MIDDLE_BRANCH
        val cycleMark = if (node.isCycle) CYCLE_SUFFIX else ""
        println("$prefix$connector${node.name}$cycleMark")
        val childPrefix = prefix + if (isLast) EMPTY_INDENT else VERTICAL_INDENT
        val children = node.dependencies
        children.forEachIndexed { index, node ->
            val childIsLast = index == children.lastIndex
            printASCIIChild(node, prefix = childPrefix, isLast = childIsLast)
        }
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
        private const val MIDDLE_BRANCH = "├── "
        private const val LAST_BRANCH = "└── "
        private const val VERTICAL_INDENT = "│   "
        private const val EMPTY_INDENT = "    "
    }
}