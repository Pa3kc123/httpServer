package sk.pa3kc.miniprojects.util

import java.io.Closeable
import java.io.InputStream
import java.lang.Exception
import java.net.SocketException
import java.net.SocketTimeoutException

class InputStreamThread(
    private val inputStream: InputStream,
    private val bufferSize: Int = 4096,
    private val onReceive: (bytes: ByteArray, byteCount: Int) -> Unit
) : () -> Unit, Closeable {
    init {
        Thread(this).also { it.start() }
    }

    override fun invoke() {
        val buffer = ByteArray(bufferSize)
        var byteCount: Int

        try {
            while (true) {
                byteCount = this.inputStream.read(buffer)

                if (byteCount == -1) break

                this.onReceive(buffer, byteCount)
            }
        } catch (e: Exception) {
            when (e) {
                !is SocketException,
                !is SocketTimeoutException -> {
                    e.printStackTrace()
                    this.close()
                }
            }
        }
    }

    override fun close() = try { this.inputStream.close() } finally {}
}
