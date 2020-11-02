package sk.pa3kc.miniprojects

import java.net.Socket
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.writeInChunks
import java.lang.Exception
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

fun handleClient(socket: Socket, finally: (Socket) -> Unit) = thread(true) {
    try {
        val currTime = System.currentTimeMillis()
        socket.soTimeout = AppConfig.server.conTimeout

        val sis = socket.getInputStream()
        val sos = socket.getOutputStream()
        val sInetAddress = socket.inetAddress

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
                return@thread
            }

            if (httpRequestBuilder.isEmpty()) {
                return@thread
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
        finally.invoke(socket)
        if (!socket.isClosed) {
            socket.close()
        }
    }
}
