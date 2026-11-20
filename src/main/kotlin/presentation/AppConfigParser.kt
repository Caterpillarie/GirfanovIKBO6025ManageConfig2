package main.kotlin.presentation

import main.kotlin.domain.AppConfig

/**
 * CLI options parser that creates [AppConfig].
 */
class AppConfigParser {
    /**
     * Transforms command line arguments to config object.
     *
     * @param args Array of arguments.
     * @return Validated object [AppConfig].
     * @throws IllegalArgumentException If format is wrong or keys are missed.
     */
    fun parse(args: Array<String>): AppConfig {
        val options = extractKeyValuePairs(args)
        validateKnownKeys(options.keys)
        return AppConfig(
            packageName = requireOption(options, KEY_PACKAGE),
            repositoryPath = requireOption(options, KEY_REPO),
            isTest = parseBoolean(options[KEY_TEST], KEY_TEST),
            isASCII = parseBoolean(options[KEY_ASCII], KEY_ASCII),
            isReverse = parseBoolean(options[KEY_REVERSE], KEY_REVERSE),
            maxDepth = parseInt(options[KEY_DEPTH], KEY_DEPTH),
        )
    }

    private fun extractKeyValuePairs(args: Array<String>): Map<String, String> {
        val result = mutableMapOf<String, String>()
        var index = START_INDEX
        while (index < args.size) {
            val rawKey = args[index]
            val key = parseKeyName(rawKey)
            val value = extractValue(args, index, rawKey)
            require(!result.containsKey(key)) {
                "Key '$rawKey' is used."
            }
            result[key] = value
            index += PAIR_STEP
        }
        return result
    }

    private fun parseKeyName(rawKey: String): String {
        require(rawKey.startsWith(PREFIX) && rawKey.length > PREFIX.length) {
            "Waited: --key, got '$rawKey'."
        }
        return rawKey.removePrefix(PREFIX)
    }

    private fun extractValue(args: Array<String>, keyIndex: Int, rawKey: String): String {
        val valueIndex = keyIndex + VALUE_OFFSET
        require(valueIndex < args.size) {
            "Got no value for key '$rawKey'."
        }
        val value = args[valueIndex]
        require(!value.startsWith(PREFIX)) {
            "Waited value for key '$rawKey', got another key '$rawKey'."
        }
        return value
    }

    private fun validateKnownKeys(keys: Set<String>) {
        val unknown = keys.minus(ALLOWED_KEYS)
        require(unknown.isEmpty()) {
            "Unknown keys: ${unknown.joinToString()}"
        }
    }

    private fun requireOption(options: Map<String, String>, key: String): String {
        return options[key] ?: throw IllegalArgumentException(
            "Key is missed: --$key"
        )
    }

    private fun parseBoolean(raw: String?, key: String): Boolean {
        if (raw == null) return false
        return when (raw.lowercase()) {
            "true" -> true
            "false" -> false
            else -> throw IllegalArgumentException(
                "Key's --$key value is not true or false, got '$raw'."
            )
        }
    }

    private fun parseInt(raw: String?, key: String): Int {
        if (raw == null) return DEFAULT_DEPTH
        return raw.toIntOrNull() ?: throw IllegalArgumentException(
            "Key --$key is not integer, got '$raw'"
        )
    }

    companion object {
        private const val PREFIX = "--"
        private const val DEFAULT_DEPTH = 3
        private const val START_INDEX = 0
        private const val PAIR_STEP = 2
        private const val VALUE_OFFSET = 1

        private const val KEY_PACKAGE = "package"
        private const val KEY_REPO = "repo"
        private const val KEY_TEST = "test"
        private const val KEY_ASCII = "ascii"
        private const val KEY_REVERSE = "reverse"
        private const val KEY_DEPTH = "depth"

        private val ALLOWED_KEYS = setOf(
            KEY_PACKAGE, KEY_REPO, KEY_TEST, KEY_ASCII, KEY_REVERSE, KEY_DEPTH
        )
    }
}