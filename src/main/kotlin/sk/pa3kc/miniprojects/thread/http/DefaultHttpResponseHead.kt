package sk.pa3kc.miniprojects.thread.http

import sk.pa3kc.miniprojects.DEFAULT_HTTP_PROTOCOL

data class DefaultHttpResponseHead(
    var protocol: String = DEFAULT_HTTP_PROTOCOL,
    var statusCode: Int = 404,
    var reasonPhrase: String = "NOT FOUND",
    val headers: MutableMap<String, String> = hashMapOf(
        "Connection" to "close"
    )
)
