package sk.pa3kc.miniprojects.data.config

import sk.pa3kc.miniprojects.util.Buildable

data class Server(
    val port: Int,
    val maxConnections: Int,
    val conTimeout: Int,
    val webDir: String
) : Buildable {
    companion object {
        @JvmField val PORT_RANGE = 0 .. 65535
    }

    class Builder : Buildable.Builder<Server>() {
        var port: Int = 8080
            set(value) {
                if (PORT_RANGE.contains(value)) {
                    field = value
                }
            }
        var maxConnections: Int = 16
        var conTimeout: Int = 10000
        var webDir: String = "/web"

        override fun build() = Server(port, maxConnections, conTimeout, webDir)
    }
}
