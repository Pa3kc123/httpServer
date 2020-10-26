package sk.pa3kc.miniprojects.util

import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaType

private val KT_BYTE = Byte::class.createType()
private val KT_SHORT = Short::class.createType()
private val KT_INT = Int::class.createType()
private val KT_LONG = Long::class.createType()
private val KT_FLOAT = Float::class.createType()
private val KT_DOUBLE = Double::class.createType()

private val KT_BYTE_ARRAY = ByteArray::class.createType()
private val KT_SHORT_ARRAY = ShortArray::class.createType()
private val KT_INT_ARRAY = IntArray::class.createType()
private val KT_LONG_ARRAY = LongArray::class.createType()
private val KT_FLOAT_ARRAY = FloatArray::class.createType()
private val KT_DOUBLE_ARRAY = DoubleArray::class.createType()

private val KT_STRING = String::class.createType()

private val KT_BUILDABLE = Buildable::class.createType()
private val KT_BUILDER = Buildable.Builder::class.createType(listOf(KTypeProjection.STAR))

interface Buildable {
    abstract class Builder<T> {
        abstract fun build(): T
    }
}

private fun <T : Buildable> getBuilderClass(cls: KClass<T>): KClass<Buildable.Builder<T>> {
    for (nestedClass in cls.nestedClasses) {
        if (nestedClass.isSubclassOf(Buildable.Builder::class)) {
            @Suppress("UNCHECKED_CAST")
            return nestedClass as KClass<Buildable.Builder<T>>
        }
    }
    throw IllegalArgumentException("${cls.qualifiedName} does not contain Builder class")
}

@Suppress("UNCHECKED_CAST")
fun <T : Buildable> loadConfig(properties: Properties, cls: KClass<T>, category: String = ""): T {
    if (!cls.isData) throw IllegalArgumentException("cls must be data class")

    val builderClass = getBuilderClass(cls)
    val builderClassInstance = builderClass.createInstance()

    for (property in builderClass.declaredMemberProperties) {
        if (property.isConst || !(property.isLateinit || property is KMutableProperty<*>)) {
            continue
        }

        val type = (property as KMutableProperty<*>).returnType

        if (type.isSubtypeOf(KT_BUILDABLE)) {
            val innerBuilderClass: KClass<Buildable.Builder<*>>

            var tmp: KClass<Buildable.Builder<*>>? = null
            for (subClass in (type.classifier as KClass<Buildable>).nestedClasses) {
                if (subClass.isSubclassOf(Buildable.Builder::class)) {
                    tmp = subClass as KClass<Buildable.Builder<*>>
                    break
                }
            }

            innerBuilderClass = tmp ?: continue

            val targetClass = (innerBuilderClass.java.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
            (property as KMutableProperty<*>).setter.call(
                builderClassInstance,
                loadConfig(
                    properties,
                    targetClass.kotlin as KClass<Buildable>,
                    "${if (category.isNotEmpty()) '.' else ""}${property.name}"
                )
            )
        } else {
            val prop = properties.getProperty("$category.${property.name}") ?: continue
            when(type) {
                KT_BYTE -> property.setter.call(builderClassInstance, prop.toByte())
                KT_SHORT -> property.setter.call(builderClassInstance, prop.toShort())
                KT_INT -> property.setter.call(builderClassInstance, prop.toInt())
                KT_LONG -> property.setter.call(builderClassInstance, prop.toLong())
                KT_FLOAT -> property.setter.call(builderClassInstance, prop.toFloat())
                KT_DOUBLE -> property.setter.call(builderClassInstance, prop.toDouble())
                KT_STRING -> property.setter.call(builderClassInstance, prop)
                KT_BYTE_ARRAY -> property.setter.call(builderClassInstance, prop.toByteArray())
                KT_SHORT_ARRAY -> property.setter.call(builderClassInstance, prop.toShortArray())
                KT_INT_ARRAY -> property.setter.call(builderClassInstance, prop.toIntArray())
                KT_LONG_ARRAY -> property.setter.call(builderClassInstance, prop.toLongArray())
                KT_FLOAT_ARRAY -> property.setter.call(builderClassInstance, prop.toFloatArray())
                KT_DOUBLE_ARRAY -> property.setter.call(builderClassInstance, prop.toDoubleArray())
                else -> {
                    if ((type.javaType as Class<*>).isArray) {
                        property.setter.call(builderClassInstance, newArray(type, prop))
                    }
                }
            }
        }
    }

    return builderClassInstance.build()
}

@PublishedApi
internal fun newArray(type: KType, prop: String): Array<Any> {
    val splits = prop.split(',')
    @Suppress("UNCHECKED_CAST")
    val arrInstance = java.lang.reflect.Array.newInstance(type.classifier?.javaClass, splits.size) as Array<Any>

    when (type.arguments.first().type) {
        KT_BYTE -> splits.forEachIndexed { index, value -> arrInstance[index] = value.toByte() }
        KT_SHORT -> splits.forEachIndexed { index, value -> arrInstance[index] = value.toShort() }
        KT_INT -> splits.forEachIndexed { index, value -> arrInstance[index] = value.toInt() }
        KT_LONG -> splits.forEachIndexed { index, value -> arrInstance[index] = value.toLong() }
        KT_FLOAT -> splits.forEachIndexed { index, value -> arrInstance[index] = value.toFloat() }
        KT_DOUBLE -> splits.forEachIndexed { index, value -> arrInstance[index] = value.toDouble() }
        KT_STRING -> splits.forEachIndexed { index, value -> arrInstance[index] = value }
        else -> throw IllegalStateException("something idk am a programmer") //TODO: Probably lazy way to do it ;)
    }

    return arrInstance
}

private fun String.toShortArray() = ShortArray(this.length) { this[it].toShort() }
private fun String.toIntArray() = IntArray(this.length) { this[it].toInt() }
private fun String.toLongArray() = LongArray(this.length) { this[it].toLong() }
private fun String.toFloatArray() = FloatArray(this.length) { this[it].toFloat() }
private fun String.toDoubleArray() = DoubleArray(this.length) { this[it].toDouble() }
