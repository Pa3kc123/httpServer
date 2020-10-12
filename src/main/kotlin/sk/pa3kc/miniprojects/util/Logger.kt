package sk.pa3kc.miniprojects.util

import java.text.SimpleDateFormat
import java.util.*

const val ANSI_ESC = 0x1B.toChar()
const val ANSI_RESET = "$ANSI_ESC[0m"

object Logger {
    private enum class LogLevel(
        val red: Int,
        val green: Int,
        val blue: Int
    ) {
        ERROR(255, 0, 0),
        WARNING(255, 255, 0),
        INFO(255, 255, 255),
        DEBUG(128, 128, 128);

        val ansiColor = "$ANSI_ESC[38;2;${this.red};${this.green};${this.blue}m"

        override fun toString() = super.name
    }

    private val DATE_FORMAT = SimpleDateFormat("[yyyy/MM/dd HH:mm:ss.SSS]")
    private var NOW: String = DATE_FORMAT.format(Date(System.currentTimeMillis()))
        get() {
            field = DATE_FORMAT.format(Date(System.currentTimeMillis()))
            return field
        }

    fun debug(msg: String? = null, ex: Throwable? = null) = log(LogLevel.DEBUG, msg, ex)
    fun info(msg: String? = null, ex: Throwable? = null) = log(LogLevel.INFO, msg, ex)
    fun warn(msg: String? = null, ex: Throwable? = null) = log(LogLevel.WARNING, msg, ex)
    fun error(msg: String? = null, ex: Throwable? = null) = log(LogLevel.ERROR, msg, ex)

    private fun log(level: LogLevel, msg: String? = null, ex: Throwable? = null) {
        NOW.also { now ->
            msg?.let { println("${level.ansiColor}$now $level: $msg$ANSI_RESET") }
            ex?.let { println("${level.ansiColor}$now $level: $ex$ANSI_RESET") }
        }
    }
}
