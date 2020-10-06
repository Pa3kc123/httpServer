package sk.pa3kc.miniprojects.http

enum class HttpMethod(val code: Int) {
    GET(0b1),
    HEAD(0b01),
    POST(0b001),
    PUT(0b0001),
    DELETE(0b00001),
    CONNECT(0b000001),
    OPTIONS(0b0000001),
    TRACE(0b00000001);

    override fun toString() = super.name
}
