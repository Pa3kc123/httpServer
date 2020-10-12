package sk.pa3kc.miniprojects.util

import sk.pa3kc.miniprojects.AppConfig
import sk.pa3kc.miniprojects.Client
import kotlin.collections.ArrayList

class ClientCollection : Collection<Client> {
    override val size = AppConfig.MAX_ALLOWED_CONNECTIONS

    private val indices = BooleanArray(this.size)
    private val clients = arrayOfNulls<Client?>(this.size)

    fun add(client: Client): Boolean {
        if (this.contains(client)) return false

        for ((i, index) in this.indices.withIndex()) {
            if (!index) {
                this.clients[i] = client
                this.indices[i] = true
                return true
            }
        }
        return false
    }
    fun remove(client: Client): Boolean {
        val index = this.indexOf(client)
        if (index == -1) return false

        this.clients[index] = null
        this.indices[index] = false
        return true
    }

    operator fun plus(client: Client) = add(client)
    operator fun minus(client: Client) = remove(client)

    override fun contains(element: Client): Boolean {
        for (client in clients) {
            if (client?.equals(element) == true) {
                return true
            }
        }
        return false
    }

    override fun containsAll(elements: Collection<Client>): Boolean {
        for (element in elements) {
            if (!this.contains(element)) {
                return false
            }
        }
        return true
    }

    override fun isEmpty(): Boolean {
        for (index in indices) {
            if (index) {
                return false
            }
        }
        return true
    }

    override fun iterator() = ClientCollectionIterator(this.clients.filterNotNull().toTypedArray())

    inner class ClientCollectionIterator(
        private val clients: Array<out Client>
    ) : Iterator<Client> {
        private var i = 0

        override fun hasNext() = i < this.clients.size

        override fun next(): Client = this.clients[i]
    }
}

fun <T> ArrayList<T>.addAll(vararg values: T) = this.addAll(values.toList())

fun ByteArray.compareRangeFromEnd(validByteCount: Int = this.size, arr: ByteArray): Boolean {
    for ((i, index) in (validByteCount - arr.size until validByteCount).withIndex()) {
        if (this[index] != arr[i]) {
            return false
        }
    }
    return true
}
