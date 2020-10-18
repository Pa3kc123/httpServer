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

/*
@Suppress("EqualsOrHashCode")
class Client(
    private val socket: Socket,
    private val onClose: (Client) -> Unit
) {
//    private val ist: InputStreamThread
    private val sis = this.socket.getInputStream()
    private val sos = this.socket.getOutputStream()

    private val data = ArrayList<Byte>()

    init {
//        this.ist = InputStreamThread(this.socket.getInputStream(), 4096, ::onBytesReceived)
    }

    fun onBytesReceived(bytes: ByteArray, byteCount: Int) {
        for (i in 0 until byteCount) {
            this.data.add(bytes[i])
        }

//        if (bytes.compareRangeFromEnd(byteCount, DELIMITER_CHECK)) {
//            onHttpRequest(newHttpRequest(this.data.toByteArray()))
//        }
    }

    fun onHttpRequest(request: HttpRequest) {
        println(request)

        val last = System.currentTimeMillis()
        val res = HttpServerThread.onHandle(request)
        res.headers["Server-Timing"] = "handle;dur=${System.currentTimeMillis() - last}"

        //TODO: Fix to not send everything in single packet
        this.sos.write(res.parse().toByteArray(Charsets.UTF_8))

        this.socket.close()
    }

    override fun toString() = this.socket.inetAddress.toString()
}
*/

fun handleClient(socket: Socket, finally: (() -> Unit)? = null) = backgroundJob {
    socket.use {
        val currTime = System.currentTimeMillis()
        it.soTimeout = AppConfig.CONNECTION_TIMEOUT

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

                    val reqCheck = httpRequestBuilder.append(String(buffer, 0, byteCount, Charsets.UTF_8))

                    if (byteCount == -1 || reqCheck) break
                }
            } catch (e: Exception) {
                when (e) {
                    is SocketTimeoutException -> Logger.info("$sInetAddress - timed out")
                    is SocketException -> Logger.warn("$sInetAddress - socket is closed")
                    else -> e.printStackTrace()
                }
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
