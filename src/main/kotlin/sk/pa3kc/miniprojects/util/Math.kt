package sk.pa3kc.miniprojects.util

fun Byte.map(start1: Byte, stop1: Byte, start2: Byte, stop2: Byte) = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2
fun Short.map(start1: Short, stop1: Short, start2: Short, stop2: Short) = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2
fun Int.map(start1: Int, stop1: Int, start2: Int, stop2: Int) = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2
fun Long.map(start1: Long, stop1: Long, start2: Long, stop2: Long) = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2
fun Float.map(start1: Float, stop1: Float, start2: Float, stop2: Float) = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2
fun Double.map(start1: Double, stop1: Double, start2: Double, stop2: Double) = (this - start1) / (stop1 - start1) * (stop2 - start2) + start2

fun Byte.map(start: Byte, stop: Byte) = if (this < start) start else if (this > stop) stop else this
fun Short.map(start: Short, stop: Short) = if (this < start) start else if (this > stop) stop else this
fun Int.map(start: Int, stop: Int) = if (this < start) start else if (this > stop) stop else this
fun Long.map(start: Long, stop: Long) = if (this < start) start else if (this > stop) stop else this
fun Float.map(start: Float, stop: Float) = if (this < start) start else if (this > stop) stop else this
fun Double.map(start: Double, stop: Double) = if (this < start) start else if (this > stop) stop else this

fun Int.map(range1: IntRange, range2: IntRange) = (this - range1.first) / (range1.last - range1.first) * (range2.last - range2.first) + range2.first
fun Long.map(range1: LongRange, range2: LongRange) = (this - range1.first) / (range1.last - range1.first) * (range2.last - range2.first) + range2.first

fun Int.map(range: IntRange) = if (this < range.first) range.first else if (this > range.last) range.last else this
fun Long.map(range: LongRange) = if (this < range.first) range.first else if (this > range.last) range.last else this
