package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL
import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER_BYTES
import sk.pa3kc.miniprojects.util.compareRangeFromEnd

interface HttpMessage {
    var headers: MutableMap<String, String>
    var body: String?

    fun statusLine(): String
    fun parse(): String
}

data class HttpRequest(
    var method: HttpMethodType = HttpMethodType.GET,
    var path: String = "index.html",
    var query: String? = null,
    var protocol: String = DEFAULT_HTTP_PROTOCOL,
    override var headers: MutableMap<String, String> = HashMap(),
    override var body: String? = null,
) : HttpMessage {
    override fun statusLine() = "$method $path${ if (query != null) "?$query" else ""} $protocol"

    override fun parse() = buildString {
        append("${statusLine()}$HTTP_LINE_BREAK")

        for (key in headers.keys) {
            append("$key: ${headers[key]}$HTTP_LINE_BREAK")
        }
        append(HTTP_LINE_BREAK)

        if (body != null) {
            append(body)
        }
    }

    override fun toString() = buildString {
        append("${statusLine()}\\r\\n${System.lineSeparator()}")

        for (key in headers.keys) {
            append("$key: ${headers[key]}\\r\\n${System.lineSeparator()}")
        }
        append("\\r\\n${System.lineSeparator()}")

        if (body != null) {
            append(body)
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
    override fun statusLine() = "$protocol ${statusCode.code} ${reasonPhrase ?: statusCode.message}"

    override fun parse() = buildString {
        append("${statusLine()}$HTTP_LINE_BREAK")

        for (key in headers.keys) {
            append("$key: ${headers[key]}$HTTP_LINE_BREAK")
        }
        append(HTTP_LINE_BREAK)

        if (body != null) {
            append(body)
        }
    }

    override fun toString() = buildString {
        append("${statusLine()}\\r\\n${System.lineSeparator()}")

        for (key in headers.keys) {
            append("$key: ${headers[key]}\\r\\n${System.lineSeparator()}")
        }
        append("\\r\\n${System.lineSeparator()}")

        if (body != null) {
            append(body)
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
    val query: String?
    val protocol: String
    val headers = HashMap<String, String>()

    headData.split(HTTP_LINE_BREAK).also { headerLines ->
        headerLines[0].split(Regex("\\s"), limit = 3).also { statusParams ->
            method = HttpMethodType.valueOf(statusParams[0])
            statusParams[1].split('?', limit = 2).also {
                path = it[0]
                query = if (it.size > 1) it[1] else null
            }
            protocol = statusParams[2]
        }

        for (i in 1 until headerLines.size) {
            headerLines[i].split(":", limit = 2).also {
                headers[it[0]] = it[1]
            }
        }
    }

    return HttpRequest(method, path, query, protocol, headers, body)
}
