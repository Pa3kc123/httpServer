package sk.pa3kc.miniprojects.util

import java.io.IOException
import java.io.OutputStream

fun arrcpy(inArr: ByteArray, outArr: ByteArray, offset: Int, length: Int) {
    if (offset == 0) return arrcpy(inArr, outArr, length)

    for (i in offset until (offset + length)) {
        outArr[i % offset] = inArr[i]
    }
}
fun arrcpy(inArr: ByteArray, outArr: ByteArray, length: Int) {
    for (i in 0 until length) {
        outArr[i] = inArr[i]
    }
}

@Throws(IOException::class)
fun OutputStream.writeInChunks(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size, chunkSize: Int = 4096) {
    if (offset > bytes.size) throw IndexOutOfBoundsException("offset is out of array bounds")
    if (offset + length > bytes.size) throw IndexOutOfBoundsException("offset + length is out of array bounds")

    val buffer = ByteArray(if (chunkSize > offset + length) offset + length else chunkSize)

    for (i in offset until (offset + length) step buffer.size) {
        arrcpy(
                bytes,
                buffer,
                i,
                if (i + buffer.size > bytes.size) bytes.size - i else buffer.size
        )

        this.write(buffer)
    }
}
