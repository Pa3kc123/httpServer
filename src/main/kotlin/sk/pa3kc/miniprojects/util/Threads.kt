package sk.pa3kc.miniprojects.util

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.nio.ByteBuffer

class InputStreamThread(
    private val inputStream: InputStream,
    private val bufferSize: Int = 4096,
    private val onReceive: (bytes: ByteArray) -> Unit,
) : () -> Unit, Closeable {
    private val thread = Thread(this).also { it.start() }
    private val buffer = ByteArray(bufferSize)

    override fun invoke() {
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
            }
        }
    }

    override fun close() = this.inputStream.close()
}
