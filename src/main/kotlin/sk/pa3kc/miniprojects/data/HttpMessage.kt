package sk.pa3kc.miniprojects.data

import sk.pa3kc.miniprojects.HTTP_LINE_BREAK
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER
import sk.pa3kc.miniprojects.HTTP_MESSAGE_DIVIDER_BYTES
import sk.pa3kc.miniprojects.util.compareRangeFromEnd

//fun newHttpRequest(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): HttpRequest {
//    if (!bytes.compareRangeFromEnd(length, HTTP_MESSAGE_DIVIDER_BYTES)) {
//        throw IllegalStateException("Incomplete request")
//    }
//
//    return newHttpRequestX(bytes, offset, length)
//}
//
//private fun newHttpRequestX(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size): HttpRequest {
//    val data = String(bytes, offset, length, Charsets.UTF_8)
//
//    val headData: String
//    val body: String?
//
//    data.split(HTTP_MESSAGE_DIVIDER, limit = 2).also {
//        headData = it[0]
//        body = if (it.size > 1) it[1] else null
//    }
//
//    val method: HttpMethodType
//    val path: String
//    val query: String?
//    val protocol: String
//    val headers = HashMap<String, String>()
//
//    headData.split(HTTP_LINE_BREAK).also { headerLines ->
//        headerLines[0].split(Regex("\\s"), limit = 3).also { statusParams ->
//            method = HttpMethodType.valueOf(statusParams[0])
//            statusParams[1].split('?', limit = 2).also {
//                path = it[0]
//                query = if (it.size > 1) it[1] else null
//            }
//            protocol = statusParams[2]
//        }
//
//        for (i in 1 until headerLines.size) {
//            headerLines[i].split(":", limit = 2).also {
//                headers[it[0]] = it[1]
//            }
//        }
//    }
//
//    val reqHead = HttpRequestHead(method, path, query, protocol, headers)
//
//    return HttpRequest(reqHead).apply {
//        if (body != null) {
//            this.onBodyContentChanged(body)
//        }
//    }
//}
