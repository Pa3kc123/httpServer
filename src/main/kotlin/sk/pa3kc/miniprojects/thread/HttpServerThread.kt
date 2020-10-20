package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.data.*
import sk.pa3kc.miniprojects.handleClient
import sk.pa3kc.miniprojects.util.Logger
import java.io.File
import java.lang.Exception
import java.net.ServerSocket
import java.net.SocketException
import java.nio.file.FileSystem
import java.nio.file.FileSystems

object HttpServerThread : Runnable, AutoCloseable {
    private val serverSocket = ServerSocket(AppConfig["server.port"] as Int)
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
                val addr = client.inetAddress

                if (clientCounter < AppConfig["server.maxConnections"] as Int) {
                    Logger.info("$addr has connected")
                    clientCounter++
                    handleClient(client) {
                        Logger.info("$addr has disconnected")
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
        File("${System.getProperty("user.dir")}/classes/web", name).let {
            Logger.debug("Trying to register site on path ${it.absolutePath}")
            if (!it.exists()) {
                Logger.warn("Failed to add static response handler - directory called \"$name\" doesn't exist")
                return
            }
            if (it.isFile) {
                Logger.warn("Failed to add static response handler - directory doesn't exist")
                return
            }

            it.list()!!.forEach { entry ->
                if (entry == "index.html") {

                }
            }

            TODO("Search for index file in site directory and make static directory handler")
        }
    }

    fun get(path: String, action: HttpAction) {
        Logger.debug("Adding new GET handler for path $path")
        RequestHandlerCollection["GET"][path] = action
    }

    fun post(path: String, action: HttpAction) {
        Logger.debug("Adding new POST handler for path $path")
        RequestHandlerCollection["POST"][path] = action
    }
}

object RequestHandlerCollection {
    private val map = HashMap<String, HashMap<String, HttpAction>>()

    fun remove(method: String): Boolean {
        val containsKey = this.map.containsKey(method)

        if (containsKey) {
            this.map.remove(method)
        }

        return containsKey
    }

    operator fun get(method: String): HashMap<String, HttpAction> {
        return this.map[method] ?: HashMap<String, HttpAction>().also {
            this.map[method] = it
        }
    }

    operator fun invoke(req: HttpRequest): HttpResponse {
        val res = HttpResponse(HttpResponseHead())
        this.map[req.head.method]?.get(req.head.path)?.invoke(req, res) ?: File("${System.getProperty("user.dir")}/classes/web", req.head.path).also {
            if (it.exists()) {
                req.body = it.readText()
            } else {
                res.head.protocol = req.head.protocol
                res.head.statusCode = 404
                res.head.reasonPhrase = "NOT FOUND"
                res.head.headers["Connection"] = "close"
            }
        }
        return res
    }
}
