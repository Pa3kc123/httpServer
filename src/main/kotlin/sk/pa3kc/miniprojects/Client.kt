package sk.pa3kc.miniprojects

import java.net.Socket
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.data.HttpResponse
import sk.pa3kc.miniprojects.thread.HttpServerThread
import sk.pa3kc.miniprojects.util.InputStreamThread
import sk.pa3kc.miniprojects.util.compareRangeFromEnd

val DELIMITER_CHECK = HTTP_MESSAGE_DIVIDER.toByteArray(Charsets.UTF_8)

class Client(
    private val socket: Socket,
    private val onClose: (Client) -> Unit
) {
    private val ist: InputStreamThread
    private val os = this.socket.getOutputStream()

    private val requestBuilder = HttpRequest.Builder()

    init {
        this.ist = InputStreamThread(this.socket.getInputStream(), 4096, ::onBytesReceived, ::onSocketClosed)
    }

    fun onBytesReceived(bytes: ByteArray, byteCount: Int) {
        this.requestBuilder.append(bytes, 0, byteCount)

//        val data = String(bytes, 0, byteCount, Charsets.UTF_8)
//        println(data)

        if (bytes.compareRangeFromEnd(byteCount, DELIMITER_CHECK)) {
            onHttpRequest(this.requestBuilder.build())
        }
    }

    fun onHttpRequest(request: HttpRequest) {
        HttpServerThread.onHandle(request)
        this.socket.close()
    }

    fun sendHttpResponse(response: HttpResponse) {

    }

    fun onSocketClosed() = onClose(this)
}
