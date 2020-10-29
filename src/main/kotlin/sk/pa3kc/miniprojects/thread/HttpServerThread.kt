package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.data.*
import sk.pa3kc.miniprojects.handleClient
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.deepList
import java.io.File
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

object HttpServerThread : Runnable, AutoCloseable {
    private val serverSocket = ServerSocket(AppConfig.server.port)
    private var clientCounter = 0

    init {
        Thread(this).also { it.start() }
    }

    fun settings(block: SettingsUpdater.() -> Unit) = SettingsUpdater.apply(block)
    internal fun onHandle(req: HttpRequest) = RequestHandlerCollection(req)

    override fun run() {
        while (true) {
            try {
                val client = this.serverSocket.accept()

                if (clientCounter < AppConfig.server.maxConnections) {
                    Logger.info("${client.inetAddress} has connected")
                    clientCounter++
                    handleClient(client) {
                        Logger.info("${it.inetAddress} has disconnected")
                        clientCounter--
                    }
                }
            } catch (e: Exception) {
                if (e is SocketException) {
                    Logger.info("Server socket was closed")
                } else {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun close() = this.serverSocket.close()
}

object SettingsUpdater {
    fun defaults(block: DefaultHttpResponseHead.() -> Unit) {
        Logger.debug("Setting new defaults")
        DefaultHttpResponseHead.apply(block)
    }

    fun static(name: String) {
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

            root.deepList()!!.forEach { file ->
                Logger.debug("Registering GET for $file")
                RequestHandlerCollection["GET", file] = HttpAction { req, res ->
                    res.body = File(root, req.head.path).readText(Charsets.UTF_8)
                }
            }
        }
    }

    fun get(path: String, action: HttpAction) {
        Logger.debug("Adding new GET handler for path $path")
        RequestHandlerCollection["GET", path] = action
    }

    fun post(path: String, action: HttpAction) {
        Logger.debug("Adding new POST handler for path $path")
        RequestHandlerCollection["POST", path] = action
    }
}

private typealias KeyType = Pair<String, String>
object RequestHandlerCollection {
    private val map = HashMap<KeyType, HttpAction>()

    fun remove(pair: KeyType): Boolean = this.map.remove(pair) != null

    operator fun get(method: String, path: String) = this[Pair(method, path)]
    operator fun get(key: KeyType): HttpAction? = this.map[key]

    operator fun set(method: String, path: String, action: HttpAction) {
        this[Pair(method, path)] = action
    }
    operator fun set(key: KeyType, action: HttpAction) {
        this.map[key] = action
    }

    operator fun invoke(req: HttpRequest): HttpResponse {
        val res = HttpResponse(HttpResponseHead())
        this.map[Pair(req.head.method, req.head.path)]?.invoke(req, res) ?: run {
            res.head.protocol = req.head.protocol
            res.head.statusCode = 404
            res.head.reasonPhrase = "NOT FOUND"
            res.head.headers["Connection"] = "close"
        }
        return res
    }
}
