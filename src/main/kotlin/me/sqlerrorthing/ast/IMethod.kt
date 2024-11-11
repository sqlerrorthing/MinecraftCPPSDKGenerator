package me.sqlerrorthing.ast


data class IMethod(
    var name: Named,
    var descriptor: String,
    var static: Boolean,
    var `final`: Boolean,
    var access: Access = Access.PUBLIC,
) {
    val parsedDescriptor: Pair<List<String>, String> get() {
        val parameterTypes = mutableListOf<String>()
        var returnType = "void"
        var i = 0
        var insideParameters = true

        while (i < descriptor.length) {
            when (descriptor[i]) {
                '(' -> insideParameters = true
                ')' -> insideParameters = false
                'B' -> if (insideParameters) parameterTypes.add("jbyte") else returnType = "jbyte"
                'C' -> if (insideParameters) parameterTypes.add("jchar") else returnType = "jchar"
                'D' -> if (insideParameters) parameterTypes.add("jdouble") else returnType = "jdouble"
                'F' -> if (insideParameters) parameterTypes.add("jfloat") else returnType = "jfloat"
                'I' -> if (insideParameters) parameterTypes.add("jint") else returnType = "jint"
                'J' -> if (insideParameters) parameterTypes.add("jlong") else returnType = "jlong"
                'S' -> if (insideParameters) parameterTypes.add("jshort") else returnType = "jshort"
                'Z' -> if (insideParameters) parameterTypes.add("jboolean") else returnType = "jboolean"
                'V' -> if (!insideParameters) returnType = "void"
                'L' -> {
                    val type = "jobject"
                    if (insideParameters) parameterTypes.add(type) else returnType = type
                    while (descriptor[i] != ';') i++
                }
                '[' -> {
                    val type = "jarray"
                    if (insideParameters) parameterTypes.add(type) else returnType = type
                }
            }
            i++
        }
        return Pair(parameterTypes, returnType)
    }
}