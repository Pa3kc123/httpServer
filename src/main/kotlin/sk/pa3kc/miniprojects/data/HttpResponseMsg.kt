package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER

class HttpResponseHead(
    var protocol: String,
    var statusCode: Int,
    var reasonPhrase: String,
    headers: MutableMap<String, String>
) : HttpHead(HttpHeadType.RESPONSE, headers) {
    val statusLine: String
        get() = "$protocol $statusCode $reasonPhrase"

    companion object {
        /**
         * Note: Must contain Http divider (\r\n\r\n)
         */
        fun parse(httpHead: String): HttpResponseHead {
            if (httpHead.indexOf(HTTP_MESSAGE_DIVIDER) == -1) throw IllegalArgumentException("httpHead must contain \\r\\n\\r\\n")

            val protocol: String
            val statusCode: Int
            val reasonPhrase: String
            val headers = HashMap<String, String>()

            httpHead.split(HTTP_LINE_BREAK).also { headerLines ->
                httpHead.split(Regex("\\s"), limit = 3).also { statusParams ->
                    protocol = statusParams[0]
                    statusCode = statusParams[1].toInt()
                    reasonPhrase = statusParams[2]
                }

                for (i in 1 until headerLines.size) {
                    headerLines[i].split(":", limit = 2).also {
                        headers[it[0]] = it[1]
                    }
                }
            }

            return HttpResponseHead(protocol, statusCode, reasonPhrase, headers)
        }
    }
}

data class HttpResponse(
    override val head: HttpResponseHead
) : HttpMessage() {
    val statusLine: String
        get() = this.head.statusLine

    override fun toHttpString() = buildString {
        append("$statusLine$HTTP_LINE_BREAK")

        val headers = this@HttpResponse.head.headers
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

        val headers = this@HttpResponse.head.headers
        for (key in headers.keys) {
            append("$key: ${headers[key]}\\r\\n${System.lineSeparator()}")
        }
        append("\\r\\n${System.lineSeparator()}")

        if (body.isNotEmpty()) {
            append(body)
        }
    }
}
