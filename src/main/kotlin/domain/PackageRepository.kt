package main.kotlin.domain

/**
 * A data source contract for retrieving information about packages and their dependencies.
 */
interface PackageRepository {
    /**
     * Checks that source exists and reachable
     */
    fun validateSource()

    /**
     * Returns a list of the names of direct dependencies for the specified package.
     */
    fun getDirectDependencies(packageName: String): List<String>
}