package sk.pa3kc.miniprojects.data.config

import sk.pa3kc.miniprojects.util.Buildable

data class Root(
    val server: Server
) : Buildable {
    class Builder : Buildable.Builder<Root>() {
        lateinit var server: Server

        override fun build() = Root(server)
    }
}
