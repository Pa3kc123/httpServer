package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.Client
import sk.pa3kc.miniprojects.data.*
import sk.pa3kc.miniprojects.util.ClientCollection
import java.lang.Exception
import java.net.ServerSocket
import java.net.SocketException

object HttpServerThread : Runnable, AutoCloseable {
    private val settingsUpdater = SettingsUpdater()
    private val thread = Thread(this)

    private val clientCollection = ClientCollection()
    private val serverSocket = ServerSocket(AppConfig.SERVER_PORT)
    private var isClosed = false

    private val requestHandlers = Array<MutableMap<String, HttpAction>>(HttpMethodType.values().size) { HashMap() }

    init {
        thread.start()
    }

    fun settings(block: SettingsUpdater.() -> Unit) = this.settingsUpdater.apply(block)
    fun onHandle(req: HttpRequest) {
        val res: HttpResponse = newHttpResponse()
        this.requestHandlers[req.method.ordinal][req.path]?.invoke(req, res) ?: res.apply {
            this.protocol = req.protocol
            this.statusCode = HttpStatusCode.NOT_FOUND
        }
        println(res)
    }

    override fun run() {
        while (true) {
            try {
                clientCollection.add(
                    Client(serverSocket.accept()) {
                        clientCollection.remove(it)
                    }
                )
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> {
                        this.close()
                    }
                }
            }
        }
    }

    override fun close() {
        this.isClosed = true
    }

    class SettingsUpdater {
        fun get(path: String, action: HttpAction) {
            requestHandlers[HttpMethodType.GET.ordinal][path] = action
        }

        fun post(path: String, action: HttpAction) {
            requestHandlers[HttpMethodType.POST.ordinal][path] = action
        }
    }
}
