package sk.pa3kc.miniprojects.util

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.Socket

abstract class SocketIO(private val socket: Socket) {
    private val ist: InputStreamThread
    private val ost: OutputStreamThread

    init {
        this.ist = InputStreamThread
    }

    abstract fun onBytesReceived(bytes: ByteArray)
    abstract fun sendBytes(bytes: ByteArray)
}

class InputStreamThread(
    private val inputStream: InputStream,
    private val bufferSize: Int = 4096,
    private val onReceive: (bytes: ByteArray) -> Unit,
    private val onClose: () -> Unit
) : () -> Unit, Closeable {
    private val buffer = ByteArray(this.bufferSize)

    override fun invoke() {
        Thread {
            var byteCount: Int
            val tmpBuffer = ByteArray(this.bufferSize)

            while (true) {
                try {
                    byteCount = this.inputStream.read(this.buffer)

                    when (byteCount) {
                        -1 -> break
                        0 -> this.onReceive(ByteArray(0))
                        this.bufferSize -> {
                            System.arraycopy(this.buffer, 0, tmpBuffer, 0, this.bufferSize)
                            this.onReceive(tmpBuffer)
                        }
                        else -> {
                            val tmpBuff = ByteArray(byteCount)
                            System.arraycopy(this.buffer, 0, tmpBuff, 0, this.bufferSize)
                            this.onReceive(tmpBuff)
                        }
                    }
                } catch (e: Exception) {
                    if (e !is IOException) {
                        e.printStackTrace()
                    }
                    this.onClose()
                }
            }
        }
    }

    override fun close() = this.inputStream.close()
}
