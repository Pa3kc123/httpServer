package sk.pa3kc.miniprojects.util

import sk.pa3kc.miniprojects.Client
import kotlin.collections.ArrayList

class ClientCollection(
    val maxSize: Int
) : Collection<Client> {
    override var size = 0

    private val indices = BooleanArray(this.maxSize)
    private val clients = arrayOfNulls<Client?>(this.maxSize)

    fun add(client: Client): Boolean {
        if (this.contains(client)) return false

        for ((i, index) in this.indices.withIndex()) {
            if (!index) {
                this.clients[i] = client
                this.indices[i] = true
                this.size++
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
        this.size--
        return true
    }

    operator fun plus(client: Client) = add(client)
    operator fun minus(client: Client) = remove(client)

    override fun contains(element: Client): Boolean {
        for (i in this.indices.indices) {
            if (this.indices[i] && this.clients[i]!! == element) {
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

    @Suppress("ReplaceSizeZeroCheckWithIsEmpty")
    override fun isEmpty(): Boolean = this.size == 0

    override fun iterator(): Iterator<Client> {
        return if (this.isEmpty()) {
            EmptyClientCollectionIterator
        } else {
            ClientCollectionIterator(this.clients.filterNotNull().toTypedArray())
        }
    }

    inner class ClientCollectionIterator(
        private val clients: Array<out Client>
    ) : Iterator<Client> {
        private var i = 0

        override fun hasNext(): Boolean = i < this.clients.size

        override fun next(): Client = if (hasNext()) this.clients[i++] else throw NoSuchElementException()
    }

    object EmptyClientCollectionIterator : Iterator<Client> {
        override fun hasNext() = false
        override fun next(): Nothing = throw NoSuchElementException()
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
