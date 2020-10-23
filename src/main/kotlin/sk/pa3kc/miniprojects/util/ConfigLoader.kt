package sk.pa3kc.miniprojects.util

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
private val KT_ARRAY = Array::class.createType(
    arguments = listOf(KTypeProjection(KVariance.INVARIANT, Any::class.createType()))
)

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

//        val prop = properties.getProperty(property.name) ?: continue
        when(val type = (property as KMutableProperty<*>).returnType) {
//            KT_BYTE -> property.setter.call(builderClassInstance, prop.toByte())
//            KT_SHORT -> property.setter.call(builderClassInstance, prop.toShort())
//            KT_INT -> property.setter.call(builderClassInstance, prop.toInt())
//            KT_LONG -> property.setter.call(builderClassInstance, prop.toLong())
//            KT_FLOAT -> property.setter.call(builderClassInstance, prop.toFloat())
//            KT_DOUBLE -> property.setter.call(builderClassInstance, prop.toDouble())
//            KT_BYTE_ARRAY -> property.setter.call(builderClassInstance, prop.toByteArray())
//            KT_SHORT_ARRAY -> property.setter.call(builderClassInstance, prop.toShortArray())
//            KT_INT_ARRAY -> property.setter.call(builderClassInstance, prop.toIntArray())
//            KT_LONG_ARRAY -> property.setter.call(builderClassInstance, prop.toLongArray())
//            KT_FLOAT_ARRAY -> property.setter.call(builderClassInstance, prop.toFloatArray())
//            KT_DOUBLE_ARRAY -> property.setter.call(builderClassInstance, prop.toDoubleArray())
//            KT_STRING -> property.setter.call(builderClassInstance, prop)
            else -> {
                if ((type.javaType as Class<*>).isArray) {
                    val splits = arrayOf("") //prop.split(',')
                    val arrClass = (Array::class.createType(arguments = type.arguments).classifier as KClass<*>)
                    val arrConstr = arrClass.constructors.first() as (Int, (Int) -> String) -> Array<String>
                    val arrInstance = arrConstr(splits.size) { splits[it] }

                    property.setter.call(builderClassInstance, arrInstance)
                } else {
                    val id = 1 // prop.split(',')[0].toInt()

                    val typeClass = (type.classifier as KClass<*>)
                    val typeConstr = typeClass.primaryConstructor
                    val arrInstance = typeConstr?.call(id) ?: continue

                    property.setter.call(builderClassInstance, arrInstance)
                }
            }
        }
    }

    return builderClassInstance.build()
}

private fun String.toShortArray() = ShortArray(this.length) { this[it].toShort() }
private fun String.toIntArray() = IntArray(this.length) { this[it].toInt() }
private fun String.toLongArray() = LongArray(this.length) { this[it].toLong() }
private fun String.toFloatArray() = FloatArray(this.length) { this[it].toFloat() }
private fun String.toDoubleArray() = DoubleArray(this.length) { this[it].toDouble() }
