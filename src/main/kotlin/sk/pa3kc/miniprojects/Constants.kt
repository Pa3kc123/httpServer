@file:JvmName("Constants")

package sk.pa3kc.miniprojects

const val CONFIG_FILE_PATH = "./config.properties"
const val CSV_DIR_PATH = "./csv"

const val HTTP_LINE_BREAK = "\r\n"
const val HTTP_MESSAGE_DIVIDER = HTTP_LINE_BREAK + HTTP_LINE_BREAK
@JvmField val HTTP_MESSAGE_DIVIDER_BYTES = HTTP_MESSAGE_DIVIDER.toByteArray(Charsets.UTF_8)

const val DEFAULT_HTTP_PROTOCOL = "HTTP/1.1"
