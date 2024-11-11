package me.sqlerrorthing.generator.impl

import me.sqlerrorthing.Config
import me.sqlerrorthing.ast.IClass
import me.sqlerrorthing.ast.IField
import me.sqlerrorthing.generator.Generator
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.util.*


private const val BASE_HEADER_NAME = "sdk.hpp"
private const val JNI_INCLUDE = "#include <jni.h>"
private const val BASE_INCLUDE = "#include <sdk.hpp>"
private const val BASE_NAMESPACE = "MinecraftSDK"

private const val BASE_WARNING = """
            // WARNING: This code was automatically generated by MinecraftCppSDK.
            // It is highly recommended to not modify this file directly.
            // Any changes made to this file will be overwritten by the next generation. 
        """//.trimIndent()

private val logger: Logger = LogManager.getLogger(CPPGenerator::class.java)


operator fun File.div(other: String): File {
    return this.resolve(other)
}

fun String.upperFirstLetter(): String {
    if (this.length <= 1) return this.uppercase(Locale.getDefault())

    return this.substring(0, 1).uppercase(Locale.getDefault()) + this.substring(1)
}



object CPPGenerator : Generator {

    override fun generate(config: Config, parsed: List<IClass>) {
        logger.debug("Generating base.hpp")
        (config.outFolder / BASE_HEADER_NAME).writeText(generateBaseHeaderFile(parsed))

        parsed.forEach {
            try {
                logger.debug("Staring generating .h class ${it.name.normalName}")
                generateClass(config, it)
            } catch (e: Exception) {
                logger.error("Failed to generate class ${it.name.normalName}", e)
            }
        }
    }

    private fun generateClass(config: Config, `class`: IClass) {

        val dstFile = config.outFolder / (`class`.name.normalName + ".hpp")
        dstFile.parentFile.mkdirs()
        dstFile.createNewFile()

        val sb = StringBuilder()
        sb.append(BASE_WARNING.trimIndent())
        sb.appendLine()
        sb.appendLine()
        logger.debug("Insert warning msg into header file")

        val (definitionPre, definitionPost) = appendDefinitionHeaderName(`class`)
        sb.append(definitionPre)
        sb.appendLine()
        sb.appendLine(BASE_INCLUDE)

        sb.appendLine()
        sb.appendLine()

        sb.append("""
            /*
             * Minecraft class
             * Original: ${`class`.name.dottedNormalName}
             * Remapped: ${`class`.name.obfuscated}
             */
            class ${`class`.name.original} final {
            public:
                static jclass self() {
                    return _self == nullptr ? _self = ${BASE_NAMESPACE}::env->FindClass("${`class`.name.obfuscated }") : _self;
                };
            
        """.trimIndent())
        sb.appendLine()
        insertFields(sb, `class`)


        sb.appendLine("""
            
            private:
                static jclass _self;
            };
        """.trimIndent())
        sb.appendLine()


        sb.append(definitionPost)
        dstFile.writeText(sb.toString())
    }

    private fun insertFields(sb: StringBuilder, `class`: IClass) {
        `class`.fields.forEach { insertField(sb, it, `class`) }
    }

    private fun insertField(sb: StringBuilder, field: IField, `class`: IClass) {

        if(!field.static)
            return

        val (cppType, second, third) = parseFieldJNIString(field)
        val isGetOrIs = if(cppType == "jboolean") "is" else "get"

        val fieldName = field.name.original
        val fieldNameUpper = fieldName.upperFirstLetter()

        sb.append("""
            |    static $cppType $isGetOrIs$fieldNameUpper() {
            |        const auto clazz = self();
            |        const auto fieldID = ${BASE_NAMESPACE}::env->GetStaticFieldID(clazz, "${field.name.obfuscated}", "${field.descriptor}");
            |        return ${BASE_NAMESPACE}::env->${second}(clazz, fieldID);
            |    };
            |
            |    static void set$fieldNameUpper(const $cppType &value) {
            |        const auto clazz = self();
            |        const auto fieldID = ${BASE_NAMESPACE}::env->GetStaticFieldID(clazz, "${field.name.obfuscated}", "${field.descriptor}");
            |        return ${BASE_NAMESPACE}::env->${third}(clazz, fieldID, value);
            |    };
            |
        """.trimMargin())
    }

    private fun parseFieldJNIString(field: IField): Triple<String, String, String> {
        val get = "Get" + (if (field.static) "Static" else "")
        val set = "Set" + (if (field.static) "Static" else "")
        if (!field.fieldInfo.isPrimitive)
            return Triple(
                "jobject",
                get + "ObjectField",
                set + "ObjectField"
            )

        val method: String = field.fieldInfo.type.upperFirstLetter() + "Field"
        val `val` = "j" + field.fieldInfo.type.lowercase()

        return Triple(
            `val`,
            get + method,
            set + method
        )
    }

    private fun appendDefinitionHeaderName(`class`: IClass) = appendDefinitionHeaderName(`class`.name.underscoredNormalName)

    private fun appendDefinitionHeaderName(name: String): Pair<String, String> {
        return with("${name.uppercase()}_HPP") {
            """
            #ifndef $this
            #define $this
        """.trimIndent() to
        """
            #endif // $this
        """.trimIndent()
        }
    }

    private fun generateBaseHeaderFile(classes: List<IClass>): String {
        val (startDef, endDef) = appendDefinitionHeaderName("MINECRAFT_SDK_BASE")

        val includes = classes.map { "#include <${it.name.normalName}.hpp>" }

        return """
            |${BASE_WARNING.trimIndent()}
            |
            |$startDef
            |
            |// define INCLUDE_ALL_CLASSES to connect all possible classes at once
            |#ifdef INCLUDE_ALL_CLASSES
            |${includes.joinToString(separator = "\n")}
            |#endif
            |
            |$JNI_INCLUDE
            |#define JNI_VERSION JNI_VERSION_1_6
            |
            |namespace $BASE_NAMESPACE {
            |
            |static JavaVM* vm {nullptr};
            |static JNIEnv* env {nullptr};
            |
            |static int InitializeSDK() {
            |    jsize count;
            |
            |    if (JNI_GetCreatedJavaVMs(&vm, 1, &count) != JNI_OK || count == 0) {
            |        return JNI_ERR;
            |    }
            |
            |    if (jint result = vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION); result == JNI_EDETACHED) {
            |      	if (result = vm->AttachCurrentThread(reinterpret_cast<void **>(&env), nullptr); result != JNI_OK) {
            |      	    return JNI_ERR;
            |      	}
            |    }
            |
            |    return JNI_OK;
            |}
            |
            |}
            |
            |$endDef
            |
            |// by sqlerrorthing with ❤️❤️❤️
        """.trimMargin()
    }
}