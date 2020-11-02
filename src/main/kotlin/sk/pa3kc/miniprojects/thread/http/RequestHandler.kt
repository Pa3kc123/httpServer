package sk.pa3kc.miniprojects.thread.http

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL
import sk.pa3kc.miniprojects.data.HttpAction
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.data.HttpResponse
import sk.pa3kc.miniprojects.data.HttpResponseHead

private typealias KeyType = Pair<String, String>

class RequestHandler {
    private val defaultHttpResponseHead = object {
        var protocol: String = DEFAULT_HTTP_PROTOCOL
        var statusCode: Int = 404
        var reasonPhrase: String = "NOT FOUND"
        val headers: MutableMap<String, String> = hashMapOf(
            "Connection" to "close"
        )
    }

    private val map = HashMap<KeyType, HttpAction>()

    fun remove(pair: KeyType): Boolean = this.map.remove(pair) != null

    operator fun get(method: String, path: String) = this[Pair(method, path)]
    operator fun get(key: KeyType): HttpAction? = this.map[key]

    operator fun set(method: String, path: String, action: HttpAction) {
        this[Pair(method, path)] = action
    }
    operator fun set(key: KeyType, action: HttpAction) {
        this.map[key] = action
    }

    operator fun invoke(req: HttpRequest): HttpResponse {
        val res = HttpResponse(HttpResponseHead())
        this.map[Pair(req.head.method, req.head.path)]?.invoke(req, res) ?: with(this.defaultHttpResponseHead) {
            res.head.protocol = protocol
            res.head.statusCode = statusCode
            res.head.reasonPhrase = reasonPhrase
            for (key in headers.keys) {
                res.head.headers[key] = headers[key]!!
            }
        }
        return res
    }
}
