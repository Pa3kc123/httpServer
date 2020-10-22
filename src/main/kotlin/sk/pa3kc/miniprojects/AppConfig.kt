package sk.pa3kc.miniprojects

import sk.pa3kc.miniprojects.util.ImmutableSet
import sk.pa3kc.miniprojects.util.Logger
import sk.pa3kc.miniprojects.util.map
import java.io.FileReader
import java.util.*
import kotlin.collections.AbstractMap
import kotlin.time.seconds

enum class PropertyName(val propName: String) {
    SERVER_PORT("server.port"),
    MAX_ALLOWED_CONNECTIONS("server.maxConnections"),
    CONNECTION_TIMEOUT("server.conTimeout"),
    SERVER_WEB_DIR("server.webDir");

    override fun toString() = super.name
}

data class ConfigMapEntry(override val key: PropertyName, override val value: Any) : Map.Entry<PropertyName, Any>

fun configSetOf(vararg pairs: Pair<PropertyName, Any>) = ImmutableSet(pairs.size) {
    ConfigMapEntry(pairs[it].first, pairs[it].second)
}

data class Configuration(
    override val entries: Set<Map.Entry<PropertyName, Any>>
) : AbstractMap<PropertyName, Any>() {
    class Builder {
        fun fromProperties(config: Properties) = Configuration(configSetOf(
            PropertyName.SERVER_PORT to config.getInt(PropertyName.SERVER_PORT, 8080, (0 .. 65535)),
            PropertyName.MAX_ALLOWED_CONNECTIONS to config.getInt(PropertyName.MAX_ALLOWED_CONNECTIONS, 16),
            PropertyName.CONNECTION_TIMEOUT to config.getInt(PropertyName.CONNECTION_TIMEOUT, 30000),
            PropertyName.SERVER_WEB_DIR to config.getString(PropertyName.SERVER_WEB_DIR, "/web")
        ))
    }
}

private fun Properties.getInt(propertyName: PropertyName, default: Int, range: IntRange? = null): Int {
    val value = this.getProperty(propertyName.propName) ?: run {
        Logger.warn("$propertyName must be defined")
        return default
    }

    val result = value.toIntOrNull() ?: run {
        Logger.warn("$propertyName must be a number")
        return default
    }

    return if (range != null) {
        result.map(range)
    } else {
        result
    }
}
private fun Properties.getLong(propertyName: PropertyName, default: Long): Long{
    val value = this.getProperty(propertyName.propName) ?: run {
        Logger.warn("$propertyName must be defined")
        return default
    }

    return value.toLongOrNull() ?: run {
        Logger.warn("$propertyName must be a number")
        return default
    }
}
private fun Properties.getString(propertyName: PropertyName, default: String): String {
    return this.getProperty(propertyName.propName) ?: run {
        Logger.warn("$propertyName must be defined")
        return default
    }
}
