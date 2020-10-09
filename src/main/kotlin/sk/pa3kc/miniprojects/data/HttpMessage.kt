package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL

interface HttpMessage {
    var statusLine: String
    var headers: Map<out String, String>
    var body: String?
}

data class HttpRequest(
    override var statusLine: String,
    override var headers: Map<out String, String>,
    override var body: String?
) : HttpMessage

data class HttpResponse(
    override var statusLine: String,
    override var headers: Map<out String, String>,
    override var body: String?
) : HttpMessage

fun newHttpRequest(
    method: HttpMethodType = HttpMethodType.GET,
    path: String = "index.html",
    protocol: String = DEFAULT_HTTP_PROTOCOL,
    headers: Map<out String, String>,
    body: String? = null
) = HttpRequest("$method $path $protocol", headers, body)

fun newHttpResponse(
    protocol: String = DEFAULT_HTTP_PROTOCOL,
    statusCode: HttpStatusCode = HttpStatusCode.OK,
    reasonPhrase: String? = null,
    headers: Map<out String, String>,
    body: String? = null
) = HttpRequest("$protocol ${statusCode.code} ${reasonPhrase ?: statusCode.message}", headers, body)

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
