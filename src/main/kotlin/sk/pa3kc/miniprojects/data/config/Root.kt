package sk.pa3kc.miniprojects.data.config

import sk.pa3kc.miniprojects.util.Buildable
import sk.pa3kc.mylibrary.Configuration

data class Root(
    val server: Server
) : Buildable {
    class Builder : Buildable.Builder<Root>() {
        lateinit var server: Server

        override fun build() = Root(server)
    }
}

@Configuration
data class Root2(
    val tmp2: Int = 80
)
