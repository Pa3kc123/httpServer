package sk.pa3kc.miniprojects.thread.http

import sk.pa3kc.miniprojects.data.HttpAction
import sk.pa3kc.miniprojects.data.HttpRequest
import sk.pa3kc.miniprojects.data.HttpResponse
import sk.pa3kc.miniprojects.data.HttpResponseHead

private typealias KeyType = Pair<String, String>

class RequestHandler {
    internal val defaultHttpResponseHead = DefaultHttpResponseHead()

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
        val res = HttpResponse(
            HttpResponseHead(
                this.defaultHttpResponseHead.protocol,
                this.defaultHttpResponseHead.statusCode,
                this.defaultHttpResponseHead.reasonPhrase,
                this.defaultHttpResponseHead.headers
            )
        )

        this.map[Pair(req.head.method, req.head.path)]?.invoke(req, res)

        return res
    }
}
