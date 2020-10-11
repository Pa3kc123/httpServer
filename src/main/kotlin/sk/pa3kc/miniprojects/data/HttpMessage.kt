package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL
import sk.pa3kc.miniprojects.DELIMITER_CHECK
import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER
import sk.pa3kc.miniprojects.util.compareRangeFromEnd
import kotlin.jvm.Throws

interface HttpMessage {
    val statusLine: String
    val headers: Map<out String, String>
    val body: String?
}

data class HttpRequest(
    val method: HttpMethodType,
    val path: String,
    val protocol: String,
    override val headers: Map<out String, String>,
    override val body: String?,
    override val statusLine: String = "$method $path $protocol"
) : HttpMessage {
    override fun toString() = buildString {
        append("$method $path $protocol ${System.lineSeparator()}")

        for ((key, value) in headers) {
            append("$key: $value${System.lineSeparator()}")
        }

        append(System.lineSeparator())
        append(body)
    }

    class Builder {
        private val bytes = ArrayList<Byte>()

        fun append(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size) {
            if (offset > bytes.size) throw IndexOutOfBoundsException("offset is outside of bounds")
            if (offset + length > bytes.size) throw IndexOutOfBoundsException("offset + length is outside of bounds")

            for (byte in bytes) {
                this.bytes.add(byte)
            }
        }

        @Throws(IllegalStateException::class)
        fun build(): HttpRequest {
            val data = String(bytes.toByteArray(), Charsets.UTF_8)

            if (!data.endsWith(HTTP_MESSAGE_DIVIDER)) {
                throw IllegalStateException("Incomplete request")
            }

            val headData: String
            val bodyData: String?

            data.split(HTTP_MESSAGE_DIVIDER, limit = 2).also {
                headData = it[0]
                bodyData = if (it.size > 1) it[1] else null
            }

            val method: HttpMethodType
            val path: String
            val protocol: String
            val headers = HashMap<String, String>()

            headData.split(HTTP_LINE_BREAK).also { headerLines ->
                headerLines[0].split("\\s", limit = 3).also {
                    method = HttpMethodType.valueOf(it[0])
                    path = it[1]
                    protocol = it[2]
                }

                for (i in 1 until headerLines.size) {
                    headerLines[i].split(":", limit = 2).also {
                        headers[it[0]] = it[1]
                    }
                }
            }

            return newHttpRequest(method, path, protocol, headers, bodyData)
        }
    }
}

data class HttpResponse(
    var protocol: String,
    var statusCode: HttpStatusCode,
    var reasonPhrase: String? = null,
    override var headers: Map<out String, String>,
    override var body: String?,
    override var statusLine: String = "$protocol ${statusCode.code} ${reasonPhrase ?: statusCode.message}"
) : HttpMessage {
    override fun toString() = buildString {
        append("$protocol $statusCode $reasonPhrase ${System.lineSeparator()}")

        for ((key, value) in headers) {
            append("$key: $value${System.lineSeparator()}")
        }

        append(System.lineSeparator())
        append(body)
    }
}

fun newHttpRequest(
    method: HttpMethodType = HttpMethodType.GET,
    path: String = "index.html",
    protocol: String = DEFAULT_HTTP_PROTOCOL,
    headers: Map<out String, String>,
    body: String? = null
) = HttpRequest(method, path, protocol, headers, body)

fun newHttpResponse(
    protocol: String = DEFAULT_HTTP_PROTOCOL,
    statusCode: HttpStatusCode = HttpStatusCode.OK,
    reasonPhrase: String? = null,
    headers: Map<out String, String> = HashMap(),
    body: String? = null
) = HttpResponse(protocol, statusCode, reasonPhrase, headers, body)

/*
class HttpRequest : HttpMessage() {
    val head = HttpRequestHead()
    var body: String? = null

    class Builder {
        private val stringBuilder = StringBuilder()

        fun append(data: String): StringBuilder = this.stringBuilder.append(data)

        @Throws(IllegalStateException::class)
        fun build() = HttpRequest().apply {
            if (stringBuilder.endsWith(HTTP_MESSAGE_DIVIDER)) {
                throw IllegalStateException("HttpRequest is not complete")
            }

            with(stringBuilder.toString().split(HTTP_MESSAGE_DIVIDER)) {
                if (this.size == 2) this@apply.body = this[1]

                with(this[0].split(HTTP_LINE_BREAK)) {
                    with(this[0].split("\\s")) {
                        this@apply.head.method = HttpMethodType.valueOf(this[0])
                        this@apply.head.path = this[1]
                        this@apply.head.protocol = this[2]
                    }

                    var header: List<String>
                    for (i in 1 until this.size) {
                        header = this[i].split("=")
                        this@apply.head.headers[header[0]] = header[1]
                    }
                }
            }
        }
    }
}

class HttpResponse {
    val head = HttpResponseHead()
    var body: String? = null

    override fun toString() = buildString {

    }

    class Builder {
        private val stringBuilder = StringBuilder()

        fun append(data: String): StringBuilder = this.stringBuilder.append(data)

        @Throws(IllegalStateException::class)
        fun build() = HttpResponse().apply {
            if (stringBuilder.endsWith(HTTP_MESSAGE_DIVIDER)) {
                throw IllegalStateException("HttpRequest is not complete")
            }

            with(stringBuilder.toString().split(HTTP_MESSAGE_DIVIDER)) {
                if (this.size == 2) this@apply.body = this[1]

                with(this[0].split(HTTP_LINE_BREAK)) {
                    with(this[0].split("\\s")) {
                        this@apply.head.method = HttpMethodType.valueOf(this[0])
                        this@apply.head.path = this[1]
                        this@apply.head.protocol = this[2]
                    }

                    var header: List<String>
                    for (i in 1 until this.size) {
                        header = this[i].split("=")
                        this@apply.head.headers[header[0]] = header[1]
                    }
                }
            }
        }
    }
}
*/
