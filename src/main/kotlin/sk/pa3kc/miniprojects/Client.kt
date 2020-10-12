package sk.pa3kc.miniprojects

import java.net.Socket
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.data.HttpResponse
import sk.pa3kc.miniprojects.data.newHttpRequest
import sk.pa3kc.miniprojects.thread.HttpServerThread
import sk.pa3kc.miniprojects.util.InputStreamThread
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.compareRangeFromEnd

val DELIMITER_CHECK = HTTP_MESSAGE_DIVIDER.toByteArray(Charsets.UTF_8)

class Client(
    private val socket: Socket,
    private val onClose: (Client) -> Unit
) {
    private val ist: InputStreamThread
    private val os = this.socket.getOutputStream()

    private val data = ArrayList<Byte>()

    init {
        this.ist = InputStreamThread(this.socket.getInputStream(), 4096, ::onBytesReceived, ::onSocketClosed)
    }

    fun onBytesReceived(bytes: ByteArray, byteCount: Int) {
        for (i in 0 until byteCount) {
            this.data.add(bytes[i])
        }

        if (bytes.compareRangeFromEnd(byteCount, DELIMITER_CHECK)) {
            onHttpRequest(newHttpRequest(this.data.toByteArray()))
        }
    }

    fun onHttpRequest(request: HttpRequest) {
        HttpServerThread.onHandle(request)
        this.socket.close()
    }

    fun onSocketClosed() = onClose(this)

    override fun toString() = this.socket.inetAddress.toString()
}
