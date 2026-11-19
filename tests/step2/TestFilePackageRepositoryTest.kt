package step2

import main.kotlin.data.TestFilePackageRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

/**
 * Unit tests for the test-mode repository [TestFilePackageRepository].
 */
class TestFilePackageRepositoryTest {

    /**
     * Verifies that the dependency graph is correctly read from a valid test file.
     */
    @Test
    fun parseValidTestFile(
    @TempDir tempDir: Path
    ) {
        val repoFile = tempDir.resolve("repo.txt")
        val content = """
            A: B C
            B: D
            C:
            D:
        """.trimIndent()
        Files.writeString(repoFile, content)
        val repository = TestFilePackageRepository(repoFile.toString())

        repository.validateSource()

        assertEquals(listOf("B", "C"), repository.getDirectDependencies("A"))
        assertEquals(listOf("D"), repository.getDirectDependencies("B"))
        assertTrue(repository.getDirectDependencies("C").isEmpty())
    }

    /**
     * Verifies that an empty list is returned when requesting a package that is not present in the file.
     */
    @Test
    fun emptyListIfNotInFile(
    @TempDir tempDir: Path
    ) {
        val repoFile = tempDir.resolve("repo.txt")
        Files.writeString(repoFile, "A: B\n")
        val repository = TestFilePackageRepository(repoFile.toString())

        val result = repository.getDirectDependencies("Z")

        assertTrue(result.isEmpty())
    }

    /**
     * Verifies that an exception is thrown if the test file does not exist.
     */
    @Test
    fun fileNotExists(@TempDir tempDir: Path) {
        val missingPath = tempDir.resolve("non_existent.txt").toString()
        val repository = TestFilePackageRepository(missingPath)

        assertThrows(IllegalArgumentException::class.java) {
            repository.validateSource()
        }
    }

    /**
     * Verifies that an exception is thrown if the provided path points to a directory rather than a file.
     */
    @Test
    fun pathIsDirectory(@TempDir tempDir: Path) {
        val repository = TestFilePackageRepository(tempDir.toString())

        assertThrows(IllegalArgumentException::class.java) {
            repository.validateSource()
        }
    }

    /**
     * Verifies that an exception is thrown when an empty test file is passed.
     */
    @Test
    fun fileIsEmpty(@TempDir tempDir: Path) {
        val emptyFile = tempDir.resolve("empty.txt")
        Files.createFile(emptyFile)
        val repository = TestFilePackageRepository(emptyFile.toString())

        assertThrows(IllegalArgumentException::class.java) {
            repository.validateSource()
        }
    }

    /**
     * Verifies that an exception is thrown when the separator ':' is missing from a line in the file.
     */
    @Test
    fun colonDelimiterIsMissing(@TempDir tempDir: Path) {
        val badFile = tempDir.resolve("bad_syntax.txt")
        Files.writeString(badFile, "A B C\n")
        val repository = TestFilePackageRepository(badFile.toString())

        assertThrows(IllegalArgumentException::class.java) {
            repository.getDirectDependencies("A")
        }
    }

    /**
     * Checks for a prohibition on package names containing lowercase letters in a test file.
     */
    @Test
    fun lowercaseLettersInPackageName(
    @TempDir tempDir: Path
    ) {
        val badFile = tempDir.resolve("lowercase.txt")
        Files.writeString(badFile, "A: b C\n")
        val repository = TestFilePackageRepository(badFile.toString())

        assertThrows(IllegalArgumentException::class.java) {
            repository.getDirectDependencies("A")
        }
    }
}