import main.kotlin.domain.AppConfig
import main.kotlin.presentation.AppConfigParser
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = AppConfigParser()
    try {
        val config = parser.parse(args)
        printConfig(config)
    } catch (e: IllegalArgumentException) {
        System.err.println(e.message)
        exitProcess(EXIT_ERROR_CODE)
    }
}

private fun printConfig(config: AppConfig) {
    println("package: ${config.packageName}")
    println("repo: ${config.repositoryPath}")
    println("test: ${config.isTest}")
    println("ascii: ${config.isASCII}")
    println("depth: ${config.maxDepth}")
}

private const val EXIT_ERROR_CODE = 1