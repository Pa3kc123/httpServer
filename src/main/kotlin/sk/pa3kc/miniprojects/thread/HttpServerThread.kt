package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.Client
import sk.pa3kc.miniprojects.util.ClientCollection
import java.lang.Exception
import java.net.ServerSocket
import java.net.SocketException
import sk.pa3kc.miniprojects.data.HttpAction
import sk.pa3kc.miniprojects.data.HttpGet
import sk.pa3kc.miniprojects.data.HttpGetAction
import sk.pa3kc.miniprojects.data.HttpMethodType

class HttpServerThread : Runnable, AutoCloseable {
    private val settingsUpdater = SettingsUpdater()
    private val thread = Thread(this)

    private val clientCollection = ClientCollection()
    private val serverSocket = ServerSocket(AppConfig.SERVER_PORT)
    private var isClosed = false

    private val requestHandlers: Array<ArrayList<HttpAction>>

    init {
        val httpMethodTypes = HttpMethodType.values()
        this.requestHandlers = Array(httpMethodTypes.size) {
            ArrayList()
        }

        thread.start()
    }

    fun settings(block: SettingsUpdater.() -> Unit) = this.settingsUpdater.apply(block)

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

    inner class SettingsUpdater {
        fun sGet(path: String, content: String) {
            requestHandlers[HttpMethodType.GET.ordinal].add(HttpGetAction())
        }
        fun dGet(path: String, action: () -> String) {

        }
        fun sPost(path: String, content: String) {

        }
        fun dPost(path: String, action: () -> String) {

        }
    }
}
