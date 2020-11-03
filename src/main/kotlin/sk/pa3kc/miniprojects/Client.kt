package sk.pa3kc.miniprojects

import java.net.Socket
import java.net.SocketException
import java.net.SocketTimeoutException
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.writeInChunks

typealias OnClientDisconnect = (Socket) -> Unit

class Client(
    private val socket: Socket,
    private val finally: OnClientDisconnect
) : Runnable {
    companion object {
        @JvmStatic
        fun handleClient(socket: Socket, finally: OnClientDisconnect) = Client(socket, finally).start()
    }

    fun start() = Thread(this).start()

    override fun run() {
        try {
            val currTime = System.currentTimeMillis()
            this.socket.soTimeout = AppConfig.server.conTimeout

            val sis = this.socket.getInputStream()
            val sos = this.socket.getOutputStream()
            val sInetAddress = this.socket.inetAddress

            val req = run {
                val httpRequestBuilder = HttpRequest.Builder()

                val buffer = ByteArray(4096)
                var byteCount: Int

                try {
                    while (true) {
                        byteCount = sis.read(buffer)

                        if (byteCount == -1) break

                        val reqCheck = httpRequestBuilder.append(String(buffer, 0, byteCount, Charsets.US_ASCII))

                        if (reqCheck) break
                    }
                } catch (e: Exception) {
                    when (e) {
                        is SocketTimeoutException -> Logger.info("$sInetAddress has timed out")
                        is SocketException -> Logger.warn("$sInetAddress socket is closed")
                        else -> e.printStackTrace()
                    }
                    return
                }

                if (httpRequestBuilder.isEmpty()) {
                    return
                }

                httpRequestBuilder.build()
            }

            val res = App.Companion.HttpServerThread.onHandle(req)
            res.head.headers["Server-Timing"] = "handle;dur=${System.currentTimeMillis() - currTime}"

            try {
                sos.writeInChunks(res.toHttpString().toByteArray(Charsets.UTF_8))
            } catch (e: Exception) {
                when (e) {
                    is SocketException -> Logger.warn("$sInetAddress - socket is closed")
                    else -> e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            when(e) {
                is SocketException -> {}
                else -> {}
            }
        } finally {
            this.finally.invoke(this.socket)
            if (!this.socket.isClosed) {
                this.socket.close()
            }
        }
    }
}
