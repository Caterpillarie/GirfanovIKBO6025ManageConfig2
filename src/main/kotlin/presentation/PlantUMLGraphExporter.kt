package main.kotlin.presentation

import main.kotlin.domain.DependencyNode

/**
 * Generates a PlantUML diagram representation of a package dependency graph.
 */
class PlantUMLGraphExporter {

    /**
     * Converts the dependency tree starting at [root] into PlantUML diagram source text.
     *
     * @param root Root node of the traversed dependency graph.
     * @return Complete PlantUML script enclosed in @startuml and @enduml tags.
     */
    fun toPlantUML(root: DependencyNode): String {
        val nodes = LinkedHashMap<String, Boolean>()
        val edges = LinkedHashSet<Pair<String, String>>()
        collectGraphElements(root, nodes, edges)

        val lines = mutableListOf(
            START_TAG,
            SKINPARAM_LINE,
            DIRECTION_LINE,
            formatRootNode(root.name)
        )
        appendChildNodes(root.name, nodes, lines)
        appendEdges(edges, nodes, lines)
        lines.add(END_TAG)
        return lines.joinToString(System.lineSeparator())
    }

    private fun collectGraphElements(
        node: DependencyNode,
        nodes: MutableMap<String, Boolean>,
        edges: MutableSet<Pair<String, String>>
    ) {
        val currentCycleState = nodes[node.name] ?: false
        nodes[node.name] = currentCycleState || node.isCycle
        for (child in node.dependencies) {
            edges.add(node.name to child.name)
            collectGraphElements(child, nodes, edges)
        }
    }

    private fun formatRootNode(packageName: String): String {
        val alias = toAlias(packageName)
        return "rectangle \"$packageName\" as $alias $ROOT_COLOR"
    }

    private fun appendChildNodes(
        rootName: String,
        nodes: Map<String, Boolean>,
        lines: MutableList<String>
    ) {
        for ((name, isCycle) in nodes) {
            if (name == rootName) {
                continue
            }
            val alias = toAlias(name)
            val colorSuffix = if (isCycle) " $CYCLE_COLOR" else ""
            lines.add("rectangle \"$name\" as $alias $colorSuffix")
        }
    }

    private fun appendEdges(
        edges: Set<Pair<String, String>>,
        nodes: Map<String, Boolean>,
        lines: MutableList<String>
    ) {
        for ((from, to) in edges) {
            val fromAlias = toAlias(from)
            val toAlias = toAlias(to)
            val isCycleTarget = nodes[to] == true
            val arrow = if (isCycleTarget) CYCLE_ARROW else NORMAL_ARROW
            val labelSuffix = if (isCycleTarget) CYCLE_EDGE_LABEL else ""
            lines.add("$fromAlias $arrow $toAlias" + if (labelSuffix.isNotEmpty()) " : $labelSuffix" else "")
        }
    }

    private fun toAlias(packageName: String): String {
        val sanitized = packageName.replace(INVALID_ALIAS_CHARS_REGEX, ALIAS_SEPARATOR)
        return "$ALIAS_PREFIX$sanitized"
    }

    companion object {
        private const val START_TAG = "@startuml"
        private const val END_TAG = "@enduml"
        private const val SKINPARAM_LINE = "skinparam componentStyle rectangle"
        private const val DIRECTION_LINE = "top to bottom direction"
        private const val ROOT_COLOR = "#D5E8D4"
        private const val CYCLE_COLOR = "#F8CECC"
        private const val NORMAL_ARROW = "-->"
        private const val CYCLE_ARROW = "..>"
        private const val CYCLE_EDGE_LABEL = "cycle"
        private const val ALIAS_PREFIX = "pkg_"
        private const val ALIAS_SEPARATOR = "_"
        private val INVALID_ALIAS_CHARS_REGEX = Regex("[^A-Za-z0-9_]")
    }
}