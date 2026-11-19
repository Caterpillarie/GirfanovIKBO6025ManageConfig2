package step4

import main.kotlin.domain.BuildDependencyGraphUseCase
import main.kotlin.domain.DependencyNode
import main.kotlin.domain.PackageRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows

/**
 * Unit test for reverse dependency graph traversal in [main.kotlin.domain.BuildDependencyGraphUseCase].
 */
class ReverseDependencyGraphTest {

    /**
     * Verifies transitive reverse dependencies for a shared leaf package.
     */
    @Test
    fun buildTransitiveReverseDependenciesGraph() {
        val repository = FakePackageRepository(createCyclicSampleGraph())
        val useCase = BuildDependencyGraphUseCase(repository)

        val root = useCase(
            rootPackage = "D",
            maxDepth = DEPTH_THREE,
            isReverse = true
        )

        assertEquals("D", root.name)
        assertEquals(listOf("B", "E"), root.dependencies.map { it.name })
        val branchB = root.dependencies[FIRST_INDEX]
        assertEquals(listOf("A"), branchB.dependencies.map { it.name })
        val nodeAFromB = branchB.dependencies[FIRST_INDEX]
        assertEquals(listOf("C"), nodeAFromB.dependencies.map { it.name })
        assertFalse(nodeAFromB.dependencies[FIRST_INDEX].isCycle)
    }

    /**
     * Verifies cycle detection and depth limiting during reverse dependency traversal.
     */
    @Test
    fun cyclicReverseDependenciesWithLimiting() {
        val repository = FakePackageRepository(createCyclicSampleGraph())
        val useCase = BuildDependencyGraphUseCase(repository)

        val root = useCase(
            rootPackage = "D",
            maxDepth = DEPTH_FOUR,
            isReverse = true
        )

        val cycleNode = findDeepestLeftNode(root)
        assertEquals("B", cycleNode.name)
        assertEquals(DEPTH_FOUR, cycleNode.depth)
        assertTrue(cycleNode.isCycle)
        assertTrue(cycleNode.dependencies.isEmpty())
    }

    /**
     * Verifies that a package with no dependencies returns an empty dependencies list.
     */
    @Test
    fun noDependencies() {
        val graph = mapOf(
            "A" to listOf("B"),
            "B" to emptyList()
        )
        val useCase = BuildDependencyGraphUseCase(FakePackageRepository(graph))

        val root = useCase(rootPackage = "A", maxDepth = DEPTH_TWO, isReverse = true)

        assertEquals("A", root.name)
        assertTrue(root.dependencies.isEmpty())
    }

    /**
     * Verifies that requesting reverse dependencies for a missing package throws an error.
     */
    @Test
    fun targetPackageNotExists() {
        val useCase = BuildDependencyGraphUseCase(
            FakePackageRepository(createCyclicSampleGraph())
        )

        assertThrows(IllegalArgumentException::class.java) {
            useCase(rootPackage = "Z", maxDepth = DEPTH_TWO, isReverse = true)
        }
    }

    private fun createCyclicSampleGraph(): Map<String, List<String>> {
        return mapOf(
            "A" to listOf("B", "E"),
            "B" to listOf("C", "D"),
            "C" to listOf("A"),
            "D" to emptyList(),
            "E" to listOf("D"),
            "F" to listOf("F")
        )
    }

    private fun findDeepestLeftNode(node: DependencyNode): DependencyNode {
        var current = node
        while (current.dependencies.isNotEmpty()) {
            current = current.dependencies[FIRST_INDEX]
        }
        return current
    }

    private class FakePackageRepository(
        private val graph: Map<String, List<String>>
    ) : PackageRepository {

        override fun validateSource() {
            return
        }

        override fun getDirectDependencies(packageName: String): List<String> {
            return graph[packageName]
                ?: throw IllegalArgumentException("Package '$packageName' not found.")
        }

        override fun getAllDependencies(): Map<String, List<String>> {
            return graph
        }
    }

    companion object {
        private const val FIRST_INDEX = 0
        private const val DEPTH_TWO = 2
        private const val DEPTH_THREE = 3
        private const val DEPTH_FOUR = 4
    }
}