package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.util.map
import java.io.FileReader
import java.util.Properties

private const val SERVER_PORT_PROP = "server.port"
private const val MAX_ALLOWED_CONNECTIONS_PROP = "server.maxConnections"

object AppConfig {
    var initialized = false
        private set
    var SERVER_PORT = 8080
        private set
    var MAX_ALLOWED_CONNECTIONS = 16
        private set

    init {
        try {
            Properties().also { config ->
                config.load(FileReader(CONFIG_FILE_PATH))

                SERVER_PORT = config.getProperty(SERVER_PORT_PROP).let {
                    it ?: throw IllegalArgumentException("$SERVER_PORT_PROP must be defined")
                    val port = it.toIntOrNull() ?: throw IllegalArgumentException("$SERVER_PORT_PROP must be a number")
                    port.map(0 .. 65535)
                }

                MAX_ALLOWED_CONNECTIONS = config.getProperty(MAX_ALLOWED_CONNECTIONS_PROP).let {
                    it ?: throw IllegalStateException("$MAX_ALLOWED_CONNECTIONS_PROP must be defined")
                    it.toIntOrNull() ?: throw IllegalStateException("$MAX_ALLOWED_CONNECTIONS_PROP must be a number")
                }
            }
            this.initialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
