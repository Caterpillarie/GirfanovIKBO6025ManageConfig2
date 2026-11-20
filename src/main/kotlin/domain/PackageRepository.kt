package main.kotlin.domain

/**
 * A data source contract for retrieving information about packages and their dependencies.
 */
interface PackageRepository {
    /**
     * Checks that source exists and reachable.
     */
    fun validateSource()

    /**
     * Returns a list of the names of direct dependencies for the specified package.
     *
     * @param packageName Target package name.
     * @return List of direct dependency package names.
     */
    fun getDirectDependencies(packageName: String): List<String>

    /**
     * Returns the complete map of all packages and their direct dependencies.
     *
     * @return Map where keys are package names and values are lists of direct dependencies.
     */
    fun getAllDependencies(): Map<String, List<String>>
}