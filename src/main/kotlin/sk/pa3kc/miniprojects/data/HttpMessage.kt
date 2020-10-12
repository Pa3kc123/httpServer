package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL
import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER_BYTES
import sk.pa3kc.miniprojects.util.compareRangeFromEnd

interface HttpMessage {
    val statusLine: String
    var headers: MutableMap<String, String>
    var body: String?

    fun parse(): String
}

data class HttpRequest(
    var method: HttpMethodType = HttpMethodType.GET,
    var path: String = "index.html",
    var protocol: String = DEFAULT_HTTP_PROTOCOL,
    override var headers: MutableMap<String, String> = HashMap(),
    override var body: String? = null,
) : HttpMessage {
    override val statusLine = "$method $path $protocol"

    override fun parse() = buildString {
        append("$method $path $protocol $HTTP_LINE_BREAK")

        for ((key, value) in headers) {
            append("$key: $value$HTTP_LINE_BREAK")
        }
        append(HTTP_LINE_BREAK)

        if (body != null) {
            append(body)
            append(HTTP_MESSAGE_DIVIDER)
        }
    }

    override fun toString() = buildString {
        append("$method $path $protocol\\r\\n${System.lineSeparator()}")

        headers.onEachIndexed { index, (key, value) ->
            append("$key: $value\\r\\n${System.lineSeparator()}")

            if (index == headers.size - 1) {
                append("\\r\\n${System.lineSeparator()}")
            }
        }

        if (body != null) {
            append(body)
            append("\\r\\n${System.lineSeparator()}".repeat(2))
        }
    }
}

data class HttpResponse(
    var protocol: String = DEFAULT_HTTP_PROTOCOL,
    var statusCode: HttpStatusCode = HttpStatusCode.OK,
    var reasonPhrase: String? = null,
    override var headers: MutableMap<String, String> = HashMap(),
    override var body: String? = null,
) : HttpMessage {
    override val statusLine: String = "$protocol ${statusCode.code} ${reasonPhrase ?: statusCode.message}"

    override fun parse() = buildString {
        append("$protocol $statusCode $reasonPhrase$HTTP_LINE_BREAK")

        for ((key, value) in headers) {
            append("$key: $value$HTTP_LINE_BREAK")
        }

        append(HTTP_MESSAGE_DIVIDER)

        if (body != null) {
            append(body)
            append(HTTP_MESSAGE_DIVIDER)
        }
    }

    override fun toString() = buildString {
        append("$protocol $statusCode $reasonPhrase\\r\\n${System.lineSeparator()}")

        headers.onEachIndexed { index, (key, value) ->
            append("$key: $value\\r\\n${System.lineSeparator()}")

            if (index == headers.size - 1) {
                append("\\r\\n${System.lineSeparator()}")
            }
        }

        if (body != null) {
            append(body)
            append("\\r\\n${System.lineSeparator()}".repeat(2))
        }
    }
}

fun newHttpRequest(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): HttpRequest {
    if (!bytes.compareRangeFromEnd(length, HTTP_MESSAGE_DIVIDER_BYTES)) {
        throw IllegalStateException("Incomplete request")
    }

    val data = String(bytes, offset, length, Charsets.UTF_8)

    val headData: String
    val body: String?

    data.split(HTTP_MESSAGE_DIVIDER, limit = 2).also {
        headData = it[0]
        body = if (it.size > 1) it[1] else null
    }

    val method: HttpMethodType
    val path: String
    val protocol: String
    val headers = HashMap<String, String>()

    headData.split(HTTP_LINE_BREAK).also { headerLines ->
        headerLines[0].split(Regex("\\s"), limit = 3).also {
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

    return HttpRequest(method, path, protocol, headers, body)
}
