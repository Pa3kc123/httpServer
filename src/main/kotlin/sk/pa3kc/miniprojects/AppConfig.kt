package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.util.ImmutableSet
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.map
import java.io.FileReader
import java.util.Properties
import kotlin.time.seconds

private const val SERVER_PORT = "server.port"
private const val MAX_ALLOWED_CONNECTIONS = "server.maxConnections"
private const val CONNECTION_TIMEOUT = "server.conTimeout"
private const val SERVER_WEB_DIR = "server.webDir"

data class ConfigMapEntry(override val key: String, override val value: Any) : Map.Entry<String, Any>

fun configSetOf(vararg pairs: Pair<String, Any>) = ImmutableSet(pairs.size) {
    ConfigMapEntry(pairs[it].first, pairs[it].second)
}

object AppConfig : AbstractMap<String, Any>() {
    override val entries: Set<Map.Entry<String, Any>>

    var initialized = false
        private set

    init {
        this.entries = configSetOf(
            SERVER_PORT to 8080,
            MAX_ALLOWED_CONNECTIONS to 16,
            CONNECTION_TIMEOUT to 30000,
            SERVER_WEB_DIR to "/web"
        )

        try {
            Properties().also { config ->
                config.load(FileReader(CONFIG_FILE_PATH))

                config.setInt(SERVER_PORT, (0 .. 65535))
                config.setInt(MAX_ALLOWED_CONNECTIONS)
                config.setInt(CONNECTION_TIMEOUT)
                config.setString(SERVER_WEB_DIR)
            }

            this.initialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override operator fun get(key: String): Any? = super.get(key)

    private fun Properties.setInt(propertyName: String, range: IntRange? = null) {
        val value = this.getProperty(propertyName) ?: run {
            Logger.warn("$propertyName must be defined")
            return
        }

        val result = value.toIntOrNull() ?: run {
            Logger.warn("$propertyName must be a number")
            return
        }

        if (range != null) {
            result.map(range)
        } else {
            result
        }
    }
    private fun Properties.setLong(propertyName: String) {
        val value = this.getProperty(propertyName) ?: run {
            Logger.warn("$propertyName must be defined")
            return
        }

        value.toLongOrNull() ?: Logger.warn("$propertyName must be a number")
    }
    private fun Properties.setString(propertyName: String) {
        this.getProperty(propertyName) ?: Logger.warn("$propertyName must be defined")
    }
}

