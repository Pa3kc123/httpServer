package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.map
import java.io.FileReader
import java.util.Properties

private const val SERVER_PORT_PROP = "server.port"
private const val MAX_ALLOWED_CONNECTIONS_PROP = "server.maxConnections"
private const val CONNECTION_TIMEOUT_PROP = "server.conTimeout"

object AppConfig {
    var initialized = false
        private set
    var SERVER_PORT = 8080
        private set
    var MAX_ALLOWED_CONNECTIONS = 16
        private set
    var CONNECTION_TIMEOUT = 30000
        private set

    init {
        try {
            Properties().also { config ->
                config.load(FileReader(CONFIG_FILE_PATH))

                config.getProperty(SERVER_PORT_PROP).let {
                    if (it == null) {
                        Logger.warn("$SERVER_PORT_PROP must be defined")
                        return@let
                    }

                    val port = it.toIntOrNull()
                    if (port == null) {
                        Logger.warn("$SERVER_PORT_PROP must be a number")
                    } else {
                        SERVER_PORT = port.map(0 .. 65535)
                    }
                }

                config.getProperty(MAX_ALLOWED_CONNECTIONS_PROP).let {
                    if (it == null) {
                        Logger.warn("$MAX_ALLOWED_CONNECTIONS_PROP must be defined")
                        return@let
                    }

                    val num = it.toIntOrNull()
                    if (num == null) {
                        Logger.warn("$MAX_ALLOWED_CONNECTIONS_PROP must be a number")
                    } else {
                        MAX_ALLOWED_CONNECTIONS = num
                    }
                }

                config.getProperty(CONNECTION_TIMEOUT_PROP).let {
                    if (it == null) {
                        Logger.info("$CONNECTION_TIMEOUT_PROP is not defined")
                        return@let
                    }

                    val timeout = it.toIntOrNull()
                    if (timeout == null) {
                        Logger.warn("$CONNECTION_TIMEOUT_PROP must be a number")
                    } else {
                        CONNECTION_TIMEOUT = timeout
                    }
                }
            }
            this.initialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
