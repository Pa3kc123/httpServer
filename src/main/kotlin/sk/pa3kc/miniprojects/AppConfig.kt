package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.data.config.Root
import sk.pa3kc.miniprojects.data.config.Server
import sk.pa3kc.miniprojects.util.loadConfig
import java.io.FileReader
import java.util.*

object AppConfig {
    private val root: Root
    val server: Server
        get() = this.root.server

    init {
        Properties().also { config ->
            config.load(FileReader(CONFIG_FILE_PATH))
            root = loadConfig(config, Root::class)
        }
    }
}
