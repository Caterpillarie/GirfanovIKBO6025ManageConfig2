package step3

import main.kotlin.domain.PackageRepository
import main.kotlin.domain.BuildDependencyGraphUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for the dependency graph construction scenario [BuildDependencyGraphUseCase].
 */
class BuildDependencyGraphUseCaseTest {

    /**
     * Verifies the construction of a multi-level graph
     * with diamond-shaped dependencies and no cycles.
     */
    @Test
    fun multiLevelGraphWithoutCycles() {
        val repository = FakePackageRepository(
            mapOf(
                "A" to listOf("B", "C"),
                "B" to listOf("D"),
                "C" to listOf("D"),
                "D" to emptyList()
            )
        )
        val useCase = BuildDependencyGraphUseCase(repository)

        val root = useCase(rootPackage = "A", maxDepth = DEFAULT_MAX_DEPTH)

        assertEquals("A", root.name)
        assertEquals(ROOT_DEPTH, root.depth)
        assertEquals(listOf("B", "C"), root.dependencies.map { it.name })
        val nodeB = root.dependencies[FIRST_INDEX]
        val nodeC = root.dependencies[SECOND_INDEX]
        assertEquals("D", nodeB.dependencies.single().name)
        assertFalse(nodeB.dependencies.single().isCycle)
        assertEquals("D", nodeC.dependencies.single().name)
        assertFalse(nodeC.dependencies.single().isCycle)
    }

    /**
     * Verifies that the recursive traversal stops upon reaching the maximum depth.
     */
    @Test
    fun stopTraversalWhenMaxDepth() {
        val repository = FakePackageRepository(
            mapOf(
                "A" to listOf("B"),
                "B" to listOf("C"),
                "C" to listOf("D"),
                "D" to emptyList()
            )
        )
        val useCase = BuildDependencyGraphUseCase(repository)

        val root = useCase(rootPackage = "A", maxDepth = LIMITED_DEPTH)

        val nodeB = root.dependencies.single()
        val nodeC = nodeB.dependencies.single()
        assertEquals(LIMITED_DEPTH, nodeC.depth)
        assertTrue(nodeC.dependencies.isEmpty())
    }

    /**
     * Verifies the detection of direct and
     * indirect cyclic dependencies without entering an infinite loop.
     */
    @Test
    fun detectCycles() {
        val repository = FakePackageRepository(
            mapOf(
                "A" to listOf("B"),
                "B" to listOf("C"),
                "C" to listOf("A")
            )
        )
        val useCase = BuildDependencyGraphUseCase(repository)

        val root = useCase(rootPackage = "A", maxDepth = DEEP_LIMIT)

        val nodeB = root.dependencies.single()
        val nodeC = nodeB.dependencies.single()
        val cycleNodeA = nodeC.dependencies.single()
        assertEquals("A", cycleNodeA.name)
        assertTrue(cycleNodeA.isCycle)
        assertTrue(cycleNodeA.dependencies.isEmpty())
    }

    /**
     * Verifies that an exception is thrown
     * when attempting to build a graph for a non-existent package.
     */
    @Test
    fun rootPackageNotExists() {
        val repository = FakePackageRepository(mapOf("A" to emptyList()))
        val useCase = BuildDependencyGraphUseCase(repository)

        assertThrows(IllegalArgumentException::class.java) {
            useCase(rootPackage = "UNKNOWN", maxDepth = DEFAULT_MAX_DEPTH)
        }
    }

    /**
     * In-memory test stub [PackageRepository].
     *
     * @property graph Packet adjacency map.
     */
    private class FakePackageRepository(
        private val graph: Map<String, List<String>>
    ) : PackageRepository {

        override fun validateSource() = Unit

        override fun getDirectDependencies(packageName: String): List<String> {
            return graph[packageName]
                ?: throw IllegalArgumentException("Package '$packageName' not found.")
        }

        override fun getAllDependencies(): Map<String, List<String>> {
            return graph
        }
    }

    companion object {
        private const val ROOT_DEPTH = 0
        private const val LIMITED_DEPTH = 2
        private const val DEFAULT_MAX_DEPTH = 5
        private const val DEEP_LIMIT = 10
        private const val FIRST_INDEX = 0
        private const val SECOND_INDEX = 1
    }
}