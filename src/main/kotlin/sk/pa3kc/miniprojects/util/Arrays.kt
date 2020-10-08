package sk.pa3kc.miniprojects.util

class FixedSizeArrayList<T>(
    private val maxCapacity: Int
) : ArrayList<T>(maxCapacity) {
    override fun add(element: T) = if (super.size < this.maxCapacity) super.add(element) else false

    override fun add(index: Int, element: T){
        if (super.size < this.maxCapacity) {
            super.add(index, element)
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        val range = super.size + elements.size

        if (range > this.maxCapacity) {
            val arr = arrayOfNulls<Any?>(range)

            var i = 0
            for (element in elements) {
                arr[i++] = element


            }
        } else super.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        return super.addAll(index, elements)
    }
}

fun <T> ArrayList<T>.addAll(vararg values: T) = this.addAll(values.toList())
