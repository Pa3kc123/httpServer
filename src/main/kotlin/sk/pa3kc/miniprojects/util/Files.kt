package sk.pa3kc.miniprojects.util

import java.io.File

fun File.deepList(): Array<String>? = this.deepListX()?.toTypedArray()

private fun File.deepListX(list: ArrayList<String> = ArrayList(), rootDir: String = ""): ArrayList<String>? {
    val entries = this.list() ?: return null

    for (entry in entries) {
        File(this.absolutePath, entry).also {
            if (it.isDirectory) {
                it.deepListX(list, "$rootDir/$entry")
            } else {
                list.add("$rootDir/$entry")
            }
        }
    }

    return list
}
