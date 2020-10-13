package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.Client
import sk.pa3kc.miniprojects.data.*
import sk.pa3kc.miniprojects.util.ClientCollection
import sk.pa3kc.miniprojects.util.Logger
import java.lang.Exception
import java.net.ServerSocket
import java.net.SocketException

object HttpServerThread : Runnable, AutoCloseable {
    private val serverSocket = ServerSocket(AppConfig.SERVER_PORT)
    private val clientCollection = ClientCollection(AppConfig.MAX_ALLOWED_CONNECTIONS)

    private val requestHandlers = Array<MutableMap<String, HttpAction>>(HttpMethodType.values().size) { HashMap() }

    init {
        Thread(this).also { it.start() }
    }

    fun settings(block: SettingsUpdater.() -> Unit) = SettingsUpdater.apply(block)
    fun onHandle(req: HttpRequest) = HttpResponse().also { res ->
        this.requestHandlers[req.method.ordinal][req.path]?.invoke(req, res) ?: res.apply {
            this.protocol = req.protocol
            this.statusCode = HttpStatusCode.NOT_FOUND
            this.headers["Connection"] = "close"
        }
    }

    override fun run() {
        while (true) {
            try {
                val clientSocket = serverSocket.accept()
                clientSocket.soTimeout = AppConfig.CONNECTION_TIMEOUT
                Logger.info("New client has connected - ${clientSocket.inetAddress}")
                clientCollection.add(
                    Client(clientSocket) {
                        Logger.info("Client $it has disconnected")
                        clientCollection.remove(it)
                    }
                )
            } catch (e: Exception) {
                if (e !is SocketException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun close() = this.serverSocket.close()

    object SettingsUpdater {
        fun get(path: String, action: HttpAction) {
            Logger.debug("Adding new GET handler for path $path")
            requestHandlers[HttpMethodType.GET.ordinal][path] = action
        }

        fun post(path: String, action: HttpAction) {
            Logger.debug("Adding new POST handler for path $path")
            requestHandlers[HttpMethodType.POST.ordinal][path] = action
        }
    }
}
