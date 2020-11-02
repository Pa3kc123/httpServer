package sk.pa3kc.miniprojects.thread.http

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.data.HttpAction
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.deepList
import java.io.File

class SettingsUpdater(val handler: RequestHandler) {
    fun defaults(block: DefaultHttpResponseHead.() -> Unit) {
        Logger.debug("Setting new defaults")
        this.handler.defaultHttpResponseHead.apply(block)
    }

    fun dirServing(name: String) {
        File(AppConfig.server.webDir, name).let { root ->
            Logger.debug("Trying to register site on path ${root.absolutePath}")
            if (!root.exists()) {
                Logger.warn("Failed to add static response handler - directory called \"$name\" doesn't exist")
                return
            }
            if (root.isFile) {
                Logger.warn("Failed to add static response handler - directory doesn't exist")
                return
            }

            for (file in root.deepList()!!) {
                Logger.debug("Registering GET for $file")
                val rootPath = root.absolutePath
                handler["GET", file] = HttpAction { req, res ->
                    res.body = File(rootPath, req.head.path).readText(Charsets.UTF_8)
                }
            }
        }
    }

    fun get(path: String, action: HttpAction) {
        Logger.debug("Adding new GET handler for path $path")
        handler["GET", path] = action
    }

    fun post(path: String, action: HttpAction) {
        Logger.debug("Adding new POST handler for path $path")
        handler["POST", path] = action
    }
}
