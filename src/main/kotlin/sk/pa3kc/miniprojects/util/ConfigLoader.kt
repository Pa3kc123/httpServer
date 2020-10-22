package sk.pa3kc.miniprojects.util

import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.*

private val KTBYTE = Byte::class.createType()
private val KTSHORT = Short::class.createType()
private val KTINT = Int::class.createType()
private val KTLONG = Long::class.createType()
private val KTFLOAT = Float::class.createType()
private val KTDOUBLE = Double::class.createType()

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

private fun <T : Buildable> getBuilderMethod(cls: KClass<Buildable.Builder<T>>): KFunction<T> {
    for (method in cls.declaredMemberFunctions) {
        if (method.name == "build") {
            @Suppress("UNCHECKED_CAST")
            return method as KFunction<T>
        }
    }
    throw IllegalArgumentException("${cls.qualifiedName} does not contain build method")
}

fun <T : Buildable> loadConfig(properties: Properties, cls: KClass<T>): T? {
    if (!cls.isData) throw IllegalArgumentException("cls must be data class")

    val builderClass = getBuilderClass(cls)
    val builderClassInstance = builderClass.createInstance()
//    val builderMethod = getBuilderMethod(builderClass)

    for (property in builderClass.declaredMemberProperties) {
        if (property.isFinal && property.isConst) {
            continue
        }

        when((property as KMutableProperty<*>).returnType) {
            KTBYTE -> property.setter.call(builderClassInstance, properties.getProperty(property.name).toByte())
            KTSHORT -> property.setter.call(builderClassInstance, properties.getProperty(property.name).toShort())
            KTINT -> property.setter.call(builderClassInstance, properties.getProperty(property.name).toInt())
            KTLONG -> property.setter.call(builderClassInstance, properties.getProperty(property.name).toLong())
            KTFLOAT -> property.setter.call(builderClassInstance, properties.getProperty(property.name).toFloat())
            KTDOUBLE -> property.setter.call(builderClassInstance, properties.getProperty(property.name).toDouble())
        }
    }

    return builderClassInstance.build()
}
