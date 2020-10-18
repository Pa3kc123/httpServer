package sk.pa3kc.miniprojects.data

fun interface HttpAction {
    operator fun invoke(req: HttpRequest, res: HttpResponse)
}

enum class HttpHeadType {
    REQUEST,
    RESPONSE;

    override fun toString() = super.name
}

abstract class HttpHead(
    protected val type: HttpHeadType,
    val headers: MutableMap<String, String>
) {
    abstract fun statusLine(): String
}

abstract class HttpMessage {
    abstract val head: HttpHead
    var hasBody = false
    var body: String
        get() = this.bodyBuilder?.toString() ?: ""
        set(value) = this.onBodyContentChanged(value)

    private var bodyBuilder: StringBuilder? = null

    open fun statusLine(): String = head.statusLine()
    open fun onBodyContentChanged(content: String) {
        if (this.bodyBuilder == null) {
            this.bodyBuilder = StringBuilder()
            this.hasBody = true
        }

        this.bodyBuilder!!.append(content)
    }

    abstract fun toHttpString(): String
}
