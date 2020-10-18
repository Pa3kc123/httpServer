package sk.pa3kc.miniprojects.util

import kotlin.collections.ArrayList

fun <T> ArrayList<T>.addAll(vararg values: T) = this.addAll(values.toList())

fun ByteArray.indexOfRange(arr: ByteArray): Int {
    if (arr.isEmpty()) return -1

    fun matchRegion(arr1: ByteArray, arr2: ByteArray, offset: Int): Boolean {
        for ((j, i) in (offset until offset + arr2.size).withIndex()) {
            if (arr1[i] != arr2[j]) {
                return false
            }
        }
        return true
    }

    for (i in this.indices) {
        if (this[i] == arr[0] && matchRegion(this, arr, i)) {
            return i
        }
    }

    return -1
}

fun ByteArray.compareRangeFromEnd(validByteCount: Int = this.size, arr: ByteArray): Boolean {
    for ((i, index) in (validByteCount - arr.size until validByteCount).withIndex()) {
        if (this[index] != arr[i]) {
            return false
        }
    }
    return true
}
