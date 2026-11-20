package step5

import main.kotlin.domain.DependencyNode
import main.kotlin.presentation.GraphPrinter
import main.kotlin.presentation.PlantUMLGraphExporter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Unit test for ASCII tree and PlantUML diagram generation.
 */
class VisualizationTest {

    private val printer = GraphPrinter()
    private val exporter = PlantUMLGraphExporter()

    /**
     * Verifies ASCII tree.
     */
    @Test
    fun treeASCII() {
        val cycleA = DependencyNode(name = "A", depth = DEPTH_TWO, isCycle = true)
        val nodeC = DependencyNode(name = "C", depth = DEPTH_TWO)
        val nodeB = DependencyNode(
            name = "B",
            depth = DEPTH_ONE,
            dependencies = listOf(nodeC, cycleA)
        )
        val nodeD = DependencyNode(name = "D", depth = DEPTH_ONE)
        val rootA = DependencyNode(
            name = "A",
            depth = DEPTH_ZERO,
            dependencies = listOf(nodeB, nodeD)
        )

        val lines = captureStdout {
            printer.print(rootA, isASCII = true)
        }

        val expected = listOf(
            "Dependency graph for a package 'A':",
            "A",
            "├── B",
            "│   ├── C",
            "│   └── A [cycle]",
            "└── D"
        )
        assertEquals(expected, lines)
    }

    /**
     * Verifies PlantUML output.
     */
    @Test
    fun validPlantUML() {
        val leafA = DependencyNode(name = "A", depth = DEPTH_TWO)
        val cycleB = DependencyNode(name = "B", depth = DEPTH_TWO, isCycle = true)
        val nodeC = DependencyNode(
            name = "C",
            depth = DEPTH_ONE,
            dependencies = listOf(leafA, cycleB)
        )
        val root = DependencyNode(
            name = "B",
            depth = DEPTH_ZERO,
            dependencies = listOf(nodeC, leafA)
        )

        val puml = exporter.toPlantUML(root)

        assertTrue(puml.startsWith("@startuml"))
        assertTrue(puml.endsWith("@enduml"))
        assertTrue(puml.contains("rectangle \"B\" as pkg_B #D5E8D4"))
        assertTrue(puml.contains("rectangle \"C\" as pkg_C"))
        assertTrue(puml.contains("rectangle \"A\" as pkg_A"))
        assertTrue(puml.contains("pkg_B --> pkg_C"))
        assertTrue(puml.contains("pkg_C ..> pkg_B : cycle"))
        assertEquals(
            SINGLE_OCCURRENCE,
            countOccurrences(puml, "pkg_C --> pkg_A"))
    }

    private fun countOccurrences(text: String, substring: String): Int {
        return text.split(substring).size - SINGLE_OCCURRENCE
    }

    private fun captureStdout(block: () -> Unit): List<String> {
        val originalOut = System.out
        val buffer = ByteArrayOutputStream()
        System.setOut(PrintStream(buffer, true, Charsets.UTF_8))
        try {
            block()
        } finally {
            System.setOut(originalOut)
        }
        return buffer.toString(Charsets.UTF_8).lines().filter { it.isNotEmpty() }
    }

    companion object {
        private const val DEPTH_ZERO = 0
        private const val DEPTH_ONE = 1
        private const val DEPTH_TWO = 2
        private const val SINGLE_OCCURRENCE = 1
    }
}