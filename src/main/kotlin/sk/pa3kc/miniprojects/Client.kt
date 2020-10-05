package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.util.SocketIO
import sk.pa3kc.miniprojects.util.SocketInputStreamThread
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class Client(private val socket: Socket) : SocketIO(socket) {
    private val sis: SocketInputStreamThread
    private val sos: SocketOutputStreamThread

    init {

    }
}
