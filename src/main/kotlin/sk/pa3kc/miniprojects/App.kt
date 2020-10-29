package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.thread.HttpServerThread
import java.io.File

class App {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
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

            if (args.contains("--preload")) {
                AppConfig
            }

            HttpServerThread.settings {
                this.defaults {
                    headers["Connection"] = "close"
                }

                this.static("site1")
            }
        }
    }
}
