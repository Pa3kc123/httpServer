package sk.pa3kc.miniprojects.util

class LimitedArrayList<T>(val maxCapacity: Int) : ArrayList<T>(maxCapacity) {
    init {
    }
}

fun <T> ArrayList<T>.addAll(vararg values: T) = this.addAll(values.toList())
