package sk.pa3kc.miniprojects

import java.net.Socket
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.data.HttpResponse
import sk.pa3kc.miniprojects.util.InputStreamThread

class Client(
    private val socket: Socket,
    private val onClose: (Client) -> Unit
) {
    private val ist = InputStreamThread(this.socket.getInputStream(), 4096, ::onBytesReceived)
    private val os = this.socket.getOutputStream()

    private val requestBuilder = HttpRequest()
    lateinit var request: HttpRequest

    fun onBytesReceived(bytes: ByteArray) {
        val data = String(bytes, Charsets.UTF_8)
        println(data)
        this.requestBuilder.append(data)

        if (data.endsWith(HTTP_MESSAGE_DIVIDER)) {
            onReceiveCompleted()
        }
    }

    open fun onReceiveCompleted() {
        onHttpRequest(this.requestBuilder.build())
    }

    open fun onHttpRequest(request: HttpRequest) {
        this.request = request;
    }

    fun sendHttpResponse(response: HttpResponse) {

    }
}
