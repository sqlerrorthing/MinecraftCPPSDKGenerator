package me.sqlerrorthing.ast

data class FieldInfo(
    val type: String,
    val arrayDimensions: Int,
    val isPrimitive: Boolean
)

class IField(
    var name: Named = Named(),
    var descriptor: String = "",
    var static: Boolean,
    var `final`: Boolean,
    var access: Access = Access.PUBLIC,
) : IAST {

    val fieldInfo: FieldInfo get() {
        var arrayDimensions = 0
        var index = 0

        while (index < descriptor.length && descriptor[index] == '[') {
            arrayDimensions++
            index++
        }

        val typeDescriptor = descriptor.substring(index)

        // Определяем тип и примитивность
        val (type, isPrimitive) = when (typeDescriptor[0]) {
            'B' -> "byte" to true
            'C' -> "char" to true
            'D' -> "double" to true
            'F' -> "float" to true
            'I' -> "int" to true
            'J' -> "long" to true
            'S' -> "short" to true
            'Z' -> "boolean" to true
            'L' -> {
                val className = typeDescriptor.substring(1, typeDescriptor.length - 1).replace('/', '.')
                className to false
            }
            else -> throw IllegalArgumentException("Неподдерживаемый тип дескриптора: $descriptor")
        }

        return FieldInfo(type, arrayDimensions, isPrimitive)
    }


}
