package main.kotlin.data

import java.io.InputStream
import java.util.zip.GZIPInputStream

/**
 * Extracts the contents of a text file from a TAR.GZ archive stream.
 */
class TarGzExtractor {

    /**
     * Finds file in TAR.GZ stream and returns its contents.
     *
     * @param inputStream Input stream of a compressed archive.
     * @param targetFileName Name of the original file within the archive.
     * @return Contents of found file.
     * @throws IllegalStateException If the file is missing within the archive.
     */
    fun extractFileText(inputStream: InputStream, targetFileName: String): String {
        GZIPInputStream(inputStream).use { gZIPInputStream ->
            val header = ByteArray(TAR_BLOCK_SIZE)
            while (readFully(gZIPInputStream, header) == TAR_BLOCK_SIZE) {
                val fileName = parseFileName(header)
                val fileSize = parseFileSize(header)
                if (fileName.isEmpty()) break
                if (fileName == targetFileName) {
                    return readContent(gZIPInputStream, fileSize)
                }
                skipEntryData(gZIPInputStream, fileSize)
            }
        }
        throw IllegalStateException("File '$targetFileName' not found")
    }

    private fun readFully(stream: InputStream, buffer: ByteArray): Int {
        var totalRead = 0
        while (totalRead < buffer.size) {
            val read = stream.read(buffer, totalRead, buffer.size - totalRead)
            if (read <= 0) break
            totalRead += read
        }
        return totalRead
    }

    private fun parseFileName(header: ByteArray): String {
        return String(header, 0, NAME_FIELD_LENGTH, Charsets.UTF_8)
            .substringBefore('\u0000')
            .trim()
    }

    private fun parseFileSize(header: ByteArray): Long {
        val rawSize = String(header, SIZE_OFFSET, SIZE_FIELD_LENGTH, Charsets.UTF_8)
            .substringBefore('\u0000')
            .trim()
        return rawSize.toLongOrNull(OCTAL_RADIX) ?: 0L
    }

    private fun readContent(stream: InputStream, size: Long): String {
        val contentBytes = ByteArray(size.toInt())
        readFully(stream, contentBytes)
        return String(contentBytes, Charsets.UTF_8)
    }

    private fun skipEntryData(stream: InputStream, fileSize: Long) {
        val remainder = fileSize % TAR_BLOCK_SIZE
        val padding = if (remainder == 0L) 0L else TAR_BLOCK_SIZE - remainder
        var bytesToSkip = fileSize + padding
        while (bytesToSkip > 0) {
            val skipped = stream.skip(bytesToSkip)
            if (skipped <= 0) break
            bytesToSkip -= skipped
        }
    }

    companion object {
        private const val TAR_BLOCK_SIZE = 512
        private const val NAME_FIELD_LENGTH = 100
        private const val SIZE_OFFSET = 124
        private const val SIZE_FIELD_LENGTH = 12
        private const val OCTAL_RADIX = 8
    }
}