package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.thread.HttpServerThread
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    with(File(CSV_DIR_PATH)) {
        if (exists()) {
            if (isFile) {
                throw IllegalStateException("Cannot create csv directory - exists file with same name")
            }
        } else {
            if (mkdirs()) {
                throw IllegalStateException("Cannot create csv directory - directory probably exists")
            }
        }
    }

    HttpServerThread.settings {
        this.get("/roots") { _, res ->
            res.body = "<h1>Hello World!</h1>"
        }
        this.post("/api/roots") { _, res ->
            res.body = "<h1>This is hidden hello world message</h1>"
        }
    }
}
