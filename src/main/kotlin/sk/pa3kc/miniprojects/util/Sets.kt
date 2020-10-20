package sk.pa3kc.miniprojects.util

class ImmutableSet<T>(override val size: Int, init: (Int) -> T) : Set<T> {
    private val arr: Array<T>

    init {
        val arr = Array<Any?>(size) {
            init(it)
        }
        @Suppress("UNCHECKED_CAST")
        this.arr = arr as Array<T>
    }

    override fun contains(element: T) = this.arr.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (!this.arr.contains(element)) return false
        }
        return true
    }

    override fun isEmpty() = this.arr.isEmpty()

    override fun iterator() = object : Iterator<T> {
        private var i = 0

        override fun hasNext() = i < this@ImmutableSet.arr.size

        override fun next() = this@ImmutableSet.arr[i++]
    }
}
