package sk.pa3kc.miniprojects.data

interface HttpAction {
    fun parseRequest(request: HttpRequest): String
    fun parseResponse(response: HttpResponse): String
}

class HttpGetAction : HttpAction {
    override fun parseRequest(request: HttpRequest): String {

    }
    override fun parseResponse(response: HttpResponse): String {
    }
}
