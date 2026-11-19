package step2

import main.kotlin.data.ApkIndexParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Unit tests for the package index parser [ApkIndexParser].
 */
class ApkIndexParserTest {

    private val parser = ApkIndexParser()

    /**
     * Checks direct dependency extraction and virtual library resolution (provides).
     */
    @Test
    fun dependencyExtractionAndProvidesResolution() {
        val indexContent = """
            P:curl
            V:8.10.1-r0
            D:ca-certificates-bundle libcurl>=8.10 so:libc.musl-x86_64.so.1 !libressl

            P:libcurl
            V:8.10.1-r0
            D:so:libc.musl-x86_64.so.1

            P:musl
            V:1.2.5-r0
            p:so:libc.musl-x86_64.so.1=1

            P:ca-certificates-bundle
            V:20240705-r0
        """.trimIndent()

        val result = parser.parseDependenciesMap(indexContent)

        assertEquals(
            listOf("ca-certificates-bundle", "libcurl", "musl"),
            result["curl"]
        )
        assertEquals(listOf("musl"), result["libcurl"])
        assertTrue(result["ca-certificates-bundle"]?.isEmpty() == true)
    }

    /**
     * Verifies the removal of duplicates and self-references from a package.
     */
    @Test
    fun removeDuplicatesAndSelfDependencies() {
        val indexContent = """
            P:busybox
            D:cmd:sh so:libc.so.1 so:libc.so.1
            p:cmd:sh=1.36 so:libc.so.1=1
        """.trimIndent()

        val result = parser.parseDependenciesMap(indexContent)

        assertTrue(result["busybox"]?.isEmpty() == true)
    }

    /**
     * Verifies that an empty map is returned for an empty input text.
     */
    @Test
    fun emptyMapForEmptyInput() {
        val result = parser.parseDependenciesMap("   \n\n  ")

        assertTrue(result.isEmpty())
    }
}