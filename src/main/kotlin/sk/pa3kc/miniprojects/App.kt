package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.thread.HttpServerThread
import sk.pa3kc.miniprojects.util.Buildable
import sk.pa3kc.miniprojects.util.loadConfig
import java.io.File
import java.io.FileReader
import java.util.*
import kotlin.system.exitProcess

data class Root(
    val server: Server
) : Buildable {
    class Builder : Buildable.Builder<Root>() {
        val serverBuilder = Server.Builder()

        override fun build() = Root(serverBuilder.build())
    }
}

data class Server(
    val port: Int,
    val maxConnections: Int,
    val conTimeout: Int,
    val webDir: String
) : Buildable {
    class Builder : Buildable.Builder<Server>() {
        var port: Int = 8080
        var maxConnections: Int = 16
        var conTimeout: Int = 10000
        var webDir: String = "/web"

        override fun build() = Server(port, maxConnections, conTimeout, webDir)
    }
}

object AppConfig {
    private val root: Root
    val server: Server
        get() = this.root.server

    init {
        Properties().also { config ->
            config.load(FileReader(CONFIG_FILE_PATH))
            root = loadConfig(config, Root::class) ?: throw IllegalStateException("Failed to load config file")
        }
    }
}

fun main(args: Array<String>) {
    AppConfig
    exitProcess(0)

    with(File(CSV_DIR_PATH)) {
        if (exists()) {
            if (isFile) {
                throw IllegalStateException("Cannot create csv directory - exists file with same name")
            }
        } else {
            if (!mkdir()) {
                throw IllegalStateException("Cannot create csv directory - directory probably exists")
            }
        }
    }

    HttpServerThread.settings {
        this.defaults {
            headers["Connection"] = "close"
        }

        this.static("site1")
    }
}
