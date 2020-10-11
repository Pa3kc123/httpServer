package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.thread.HttpServerThread
import java.io.File

fun main(args: Array<String>) {
    with(File(CSV_DIR_PATH)) {
        val exists = exists()
        if (exists && isFile) throw IllegalStateException("Cannot create csv directory - exists file with same name")
        if (!exists && (mkdir() || mkdirs())) throw IllegalStateException("Cannot create csv directory - directory probably exists")
    }

    HttpServerThread.settings {
        this.get("/roots") { _, res ->
            res.body = "<h1>Hello World!</h1>"
        }
    }
}
