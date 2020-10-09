package sk.pa3kc.miniprojects.data

const val ERR_MSG = "Oopsie Woopsie! UwU Somebody made a fucky wucky!! A wittle fucko boingo! The code monkeys should be working VEWY HAWD to fix this!"

enum class HttpMethodType {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    CONNECT,
    OPTIONS,
    TRACE;

    override fun toString() = super.name
}

interface HttpMethod {
    val type: HttpMethodType
    val bodyIgnored: Boolean
}

class HttpGet : HttpMethod {
    override val type = HttpMethodType.GET
    override val bodyIgnored = true
}

class HttpPost : HttpMethod {
    override val type = HttpMethodType.POST
    override val bodyIgnored = false
}

/*
data class HttpGet(
    override val method: HttpMethodType,
    override val path: String,
    override val action: () -> String
) : AbstractHttpMethod {
    class Builder {
        var path = ""
        var action: (() -> String)? = null

        fun build() = HttpGet(HttpMethodType.GET, this.path, action ?: {"<h1>$ERR_MSG</h1>"})
    }
}
*/
