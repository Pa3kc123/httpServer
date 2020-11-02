package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.thread.http.HttpServerImpl
import java.io.File

class App {
    companion object {
        object HttpServerThread : HttpServerImpl()

        @JvmStatic
        fun main(args: Array<String>) {
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

            if ("--preload" in args) {
                AppConfig
            }

            HttpServerThread.settings {
                this.defaults {
                    headers["Connection"] = "close"
                }

                this.dirServing("site1")
            }

            HttpServerThread.initialized = true
        }
    }
}
