package sk.pa3kc.miniprojects.thread

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.Client
import sk.pa3kc.miniprojects.util.LimitedArrayList
import java.lang.Exception
import java.net.ServerSocket
import java.net.SocketException

class HttpServerThread : () -> Unit, AutoCloseable {
    private val thread = Thread(this)

    private val clientCollection = LimitedArrayList<Client>(AppConfig.MAX_ALLOWED_CONNECTIONS)
    private val serverSocket = ServerSocket(AppConfig.SERVER_PORT)
    private var isClosed = false

    init {
        thread.start()
    }

    override fun invoke() {
        while (true) {
            try {
                clientCollection += Client(serverSocket.accept())
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
}
