package sk.pa3kc.miniprojects.thread.http

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.data.HttpResponse
import sk.pa3kc.miniprojects.handleClient
import sk.pa3kc.miniprojects.util.Logger
import java.io.Closeable
import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException

open class HttpServerImpl : Runnable, Closeable {
    private val handler = RequestHandler()
    private val settingsUpdater = SettingsUpdater(this.handler)

    private lateinit var serverSocket: ServerSocket
    private var clientCounter = 0

    var initialized = false
        @Throws(IOException::class)
        set(value) {
            if (!field && value) {
                try {
                    serverSocket = ServerSocket(AppConfig.server.port)
                } catch (e: IOException) {
                    throw e
                }

                field = value
            }
        }

    fun start() {
        Thread(this).start()
    }

    fun settings(block: SettingsUpdater.() -> Unit) = this.settingsUpdater.apply(block)
    internal fun onHandle(req: HttpRequest): HttpResponse {
        return this.handler(req)
    }

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

    override fun close() {
        try {
            this.serverSocket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
