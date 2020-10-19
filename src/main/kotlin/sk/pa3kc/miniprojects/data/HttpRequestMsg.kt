package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL
import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER

class HttpRequestHead(
    var method: String,
    var path: String = "index.html",
    var query: String? = null,
    var protocol: String = DEFAULT_HTTP_PROTOCOL,
    headers: MutableMap<String, String>
) : HttpHead(HttpHeadType.REQUEST, headers) {
    override val statusLine: String
        get() = "$method $path${if (query != null) "?$query" else ""} $protocol"

    companion object {
        fun parse(httpHead: String): HttpRequestHead {
            val method: String
            val path: String
            val query: String?
            val protocol: String
            val headers = HashMap<String, String>()

            httpHead.split(HTTP_LINE_BREAK).also { headerLines ->
                headerLines[0].split(Regex("\\s"), limit = 3).also { statusParams ->
                    method = statusParams[0]
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

            return HttpRequestHead(method, path, query, protocol, headers)
        }
    }
}

data class HttpRequest(
    override val head: HttpRequestHead
) : HttpMessage() {
    override val statusLine: String
        get() = this.head.statusLine

    override fun toHttpString() = buildString {
        append("$statusLine$HTTP_LINE_BREAK")

        val headers = this@HttpRequest.head.headers
        for (key in headers.keys) {
            append("$key: ${headers[key]}$HTTP_LINE_BREAK")
        }
        append(HTTP_LINE_BREAK)

        if (body.isNotEmpty()) {
            append(body)
        }
    }

    override fun toString() = buildString {
        append("$statusLine\\r\\n${System.lineSeparator()}")

        val headers = this@HttpRequest.head.headers
        for (key in headers.keys) {
            append("$key: ${headers[key]}\\r\\n${System.lineSeparator()}")
        }
        append("\\r\\n${System.lineSeparator()}")

        if (body.isNotEmpty()) {
            append(body)
        }
    }

    class Builder {
        private val builder = StringBuilder()
        private var req: HttpRequest? = null

        fun isEmpty() = this.builder.isEmpty()

        /**
         * @return true if valid [HttpRequest] can be build, false otherwise
         */
        fun append(msgChunk: String): Boolean {
            this.req?.onBodyContentChanged(msgChunk) ?: builder.append(msgChunk)
            return msgChunk.indexOf(HTTP_MESSAGE_DIVIDER) != -1
        }

        fun build(): HttpRequest {
            if (this.builder.isEmpty()) throw IllegalStateException("Cannot build HttpRequest without request data")

            this.builder.toString().split(HTTP_MESSAGE_DIVIDER, limit = 2).also { splits ->
                val head = HttpRequestHead.parse(splits[0])

                return HttpRequest(head).also {
                    this@Builder.req = it
                    this@Builder.req!!.onBodyContentChanged(splits[1])
                }
            }
        }
    }
}
