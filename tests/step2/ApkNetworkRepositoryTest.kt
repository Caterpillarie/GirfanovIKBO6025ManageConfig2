package step2

import main.kotlin.data.ApkNetworkRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.zip.GZIPOutputStream

/**
 * Unit test for repository [ApkNetworkRepository].
 */
class ApkNetworkRepositoryTest {

    /**
     * Verifies the loading of the index based on
     * the directory's base URL and the return of dependencies.
     */
    @Test
    fun loadIndexAndReturnDependencies(
    @TempDir tempDir: Path
    ) {
        val indexText = "P:git\nD:curl pcre2\n\nP:curl\n\nP:pcre2\n"
        createArchiveFile(tempDir.resolve("APKINDEX.tar.gz").toFile(), indexText)
        val repoUrl = tempDir.toUri().toString()
        val repository = ApkNetworkRepository(repoUrl)

        repository.validateSource()
        val deps = repository.getDirectDependencies("git")

        assertEquals(listOf("curl", "pcre2"), deps)
    }

    /**
     * Verifies that an exception is thrown when requesting a non-existent package.
     */
    @Test
    fun packageNotInIndex(
    @TempDir tempDir: Path
    ) {
        val indexText = "P:curl\nV:1.0\n"
        createArchiveFile(tempDir.resolve("APKINDEX.tar.gz").toFile(), indexText)
        val repository = ApkNetworkRepository(tempDir.toUri().toString())

        assertThrows(IllegalArgumentException::class.java) {
            repository.getDirectDependencies("non-existent-pkg")
        }
    }

    /**
     * Verifies that an exception is thrown
     * when no archive exists at the specified location.
     */
    @Test
    fun archiveDontExist(
    @TempDir tempDir: Path
    ) {
        val repository = ApkNetworkRepository(tempDir.toUri().toString())

        assertThrows(IllegalArgumentException::class.java) {
            repository.validateSource()
        }
    }

    private fun createArchiveFile(targetFile: File, indexContent: String) {
        val data = indexContent.toByteArray(Charsets.UTF_8)
        GZIPOutputStream(FileOutputStream(targetFile)).use { gzip ->
            val header = ByteArray(TAR_BLOCK_SIZE)
            "APKINDEX".toByteArray(Charsets.UTF_8).copyInto(header, 0)
            val octalSize = data.size
                .toString(OCTAL_RADIX)
                .padStart(SIZE_DIGITS_LENGTH, '0')
            octalSize.toByteArray(Charsets.UTF_8).copyInto(header, SIZE_OFFSET)
            gzip.write(header)
            gzip.write(data)
            val remainder = data.size % TAR_BLOCK_SIZE
            if (remainder != 0) {
                gzip.write(ByteArray(TAR_BLOCK_SIZE - remainder))
            }
            gzip.write(ByteArray(TAR_BLOCK_SIZE * END_BLOCKS_COUNT))
        }
    }

    companion object {
        private const val TAR_BLOCK_SIZE = 512
        private const val END_BLOCKS_COUNT = 2
        private const val SIZE_OFFSET = 124
        private const val SIZE_DIGITS_LENGTH = 11
        private const val OCTAL_RADIX = 8
    }
}