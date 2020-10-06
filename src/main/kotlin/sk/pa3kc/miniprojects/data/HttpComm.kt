package sk.pa3kc.miniprojects.data

import java.lang.StringBuilder
import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER
import sk.pa3kc.miniprojects.http.HttpCode
import sk.pa3kc.miniprojects.http.HttpMethod

class HttpRequest {
    val head = HttpRequestHead()
    var body: String? = null

    class Builder {
        private val stringBuilder = StringBuilder()

        fun append(data: String) = this.stringBuilder.append(data)

        @Throws(IllegalStateException::class)
        fun build() = HttpRequest().apply {
            if (stringBuilder.endsWith(HTTP_MESSAGE_DIVIDER)) {
                throw IllegalStateException("HttpRequest is not complete")
            }

            with(stringBuilder.toString().split(HTTP_MESSAGE_DIVIDER)) {
                if (this.size == 2) this@apply.body = this[1]

                with(this[0].split(HTTP_LINE_BREAK)) {
                    with(this[0].split("\\s")) {
                        this@apply.head.method = HttpMethod.valueOf(this[0])
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

        fun append(data: String) = this.stringBuilder.append(data)

        @Throws(IllegalStateException::class)
        fun build() = HttpResponse().apply {
            if (stringBuilder.endsWith(HTTP_MESSAGE_DIVIDER)) {
                throw IllegalStateException("HttpRequest is not complete")
            }

            with(stringBuilder.toString().split(HTTP_MESSAGE_DIVIDER)) {
                if (this.size == 2) this@apply.body = this[1]

                with(this[0].split(HTTP_LINE_BREAK)) {
                    with(this[0].split("\\s")) {
                        this@apply.head.method = HttpMethod.valueOf(this[0])
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

typealias babyObj = {

}

class HttpRequestHead {
    var method = HttpMethod.GET
    var path = "/index.html"
    var protocol = "HTTP/1.1"
    var headers = HashMap<String, String>()
}

class HttpResponseHead {
    var protocol = "HTTP/1.1"
    var responseCode = HttpCode.NOT_FOUND
    var headers = HashMap<String, String>()
}
