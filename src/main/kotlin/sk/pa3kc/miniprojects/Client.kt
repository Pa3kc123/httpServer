package sk.pa3kc.miniprojects

import java.net.Socket
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.thread.HttpServerThread
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.backgroundJob
import sk.pa3kc.miniprojects.util.writeInChunks
import java.lang.Exception
import java.net.SocketException
import java.net.SocketTimeoutException

fun handleClient(socket: Socket, finally: (() -> Unit)? = null) = backgroundJob {
    socket.use {
        val currTime = System.currentTimeMillis()
        it.soTimeout = AppConfig["server.conTimeout"] as Int

        val sis = it.getInputStream()
        val sos = it.getOutputStream()
        val sInetAddress = it.inetAddress

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
                return@use
            }

            if (httpRequestBuilder.isEmpty()) {
                return@use
            }

            httpRequestBuilder.build()
        }

        val res = HttpServerThread.onHandle(req)
        res.head.headers["Server-Timing"] = "handle;dur=${System.currentTimeMillis() - currTime}"

        try {
            sos.writeInChunks(res.toHttpString().toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            when (e) {
                is SocketException -> Logger.warn("$sInetAddress - socket is closed")
                else -> e.printStackTrace()
            }
            return@use
        }
    }

    finally?.invoke()
}
