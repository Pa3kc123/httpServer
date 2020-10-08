package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.Client
import sk.pa3kc.miniprojects.util.ClientCollection
import java.lang.Exception
import java.net.ServerSocket
import java.net.SocketException

class HttpServerThread : () -> Unit, AutoCloseable {
    private val thread = Thread(this)

    private val clientCollection = ClientCollection()
    private val serverSocket = ServerSocket(AppConfig.SERVER_PORT)
    private var isClosed = false

    private val getHandlers = ArrayList<() -> Unit>()

    init {
        thread.start()
    }

    fun settings(block: SettingsUpdater.() -> Unit) = SettingsUpdater().apply(block).applyChanges()


    override fun invoke() {
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
        fun addGet(getHandler: () -> Unit) {
            getHandlers.add(getHandler)
        }

        fun applyChanges() {

        }
    }
}
