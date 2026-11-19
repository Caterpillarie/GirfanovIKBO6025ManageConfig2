package step2

import main.kotlin.data.TarGzExtractor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

/**
 * Unit tests for an archive extractor [TarGzExtractor].
 */
class TarGzExtractorTest {

    private val extractor = TarGzExtractor()

    /**
     * Verifies the extraction of the target file after skipping the preceding file, including its padding.
     */
    @Test
    fun targetFileExtractionAfterSkipping() {
        val expectedContent = "P:alpine-baselayout\nV:3.6.5-r0\n"
        val archiveBytes = buildTarGzArchive(
            "DESCRIPTION" to "Some description text with non-aligned length",
            "APKINDEX" to expectedContent
        )

        val actualContent = extractor.extractFileText(
            ByteArrayInputStream(archiveBytes),
            "APKINDEX"
        )

        assertEquals(expectedContent, actualContent)
    }

    /**
     * Verifies that an exception is thrown if the requested file is missing from the archive.
     */
    @Test
    fun targetFileIsMissingInArchive() {
        val archiveBytes = buildTarGzArchive("OTHER_FILE" to "data")

        assertThrows(IllegalStateException::class.java) {
            extractor.extractFileText(ByteArrayInputStream(archiveBytes), "APKINDEX")
        }
    }

    private fun buildTarGzArchive(vararg entries: Pair<String, String>): ByteArray {
        val byteOut = ByteArrayOutputStream()
        GZIPOutputStream(byteOut).use { gzip ->
            for ((name, content) in entries) {
                writeTarEntry(gzip, name, content.toByteArray(Charsets.UTF_8))
            }
            gzip.write(ByteArray(TAR_BLOCK_SIZE * END_BLOCKS_COUNT))
        }
        return byteOut.toByteArray()
    }

    private fun writeTarEntry(gzip: GZIPOutputStream, name: String, data: ByteArray) {
        val header = ByteArray(TAR_BLOCK_SIZE)
        val nameBytes = name.toByteArray(Charsets.UTF_8)
        nameBytes.copyInto(header, destinationOffset = 0)
        val octalSize = data.size.toString(OCTAL_RADIX).padStart(SIZE_DIGITS_LENGTH, '0')
        octalSize.toByteArray(Charsets.UTF_8).copyInto(header, destinationOffset = SIZE_OFFSET)
        gzip.write(header)
        gzip.write(data)
        val remainder = data.size % TAR_BLOCK_SIZE
        if (remainder != 0) {
            gzip.write(ByteArray(TAR_BLOCK_SIZE - remainder))
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