import main.kotlin.data.ApkNetworkRepository
import main.kotlin.data.TestFilePackageRepository
import main.kotlin.domain.AppConfig
import main.kotlin.domain.BuildDependencyGraphUseCase
import main.kotlin.domain.DependencyNode
import main.kotlin.domain.PackageRepository
import main.kotlin.presentation.AppConfigParser
import main.kotlin.presentation.GraphPrinter
import main.kotlin.presentation.PlantUMLGraphExporter
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    System.setOut(PrintStream(System.out, true, Charsets.UTF_8))
    System.setErr(PrintStream(System.err, true, Charsets.UTF_8))
    val parser = AppConfigParser()
    try {
        val config = parser.parse(args)
        val repository = createRepository(config)
        val useCase = BuildDependencyGraphUseCase(repository)
        val graph = useCase(config.packageName, config.maxDepth, config.isReverse)
        GraphPrinter().print(root = graph, isASCII = config.isASCII)
        printPlantUMLDiagram(graph)
    } catch (e: IllegalArgumentException) {
        System.err.println(e.message)
        exitProcess(EXIT_ERROR_CODE)
    }
}

private fun printPlantUMLDiagram(graph: DependencyNode) {
    val diagramText = PlantUMLGraphExporter().toPlantUML(graph)
    println()
    println("Graph description on PlantUML:")
    println(diagramText)
}

private fun createRepository(config: AppConfig): PackageRepository {
    val repository = if (config.isTest) {
        TestFilePackageRepository(config.repositoryPath)
    } else {
        ApkNetworkRepository(config.repositoryPath)
    }
    repository.validateSource()
    return repository
}

private fun printDirectDependencies(packageName: String, dependencies: List<String>) {
    println("'$packageName' package's direct dependencies:")
    if (dependencies.isEmpty()) {
        println(" (got no dependencies)")
        return
    }
    dependencies.forEach { dep -> println(" - $dep") }
}

private fun printConfig(config: AppConfig) {
    println("package: ${config.packageName}")
    println("repo: ${config.repositoryPath}")
    println("test: ${config.isTest}")
    println("ascii: ${config.isASCII}")
    println("depth: ${config.maxDepth}")
}

private const val EXIT_ERROR_CODE = 1