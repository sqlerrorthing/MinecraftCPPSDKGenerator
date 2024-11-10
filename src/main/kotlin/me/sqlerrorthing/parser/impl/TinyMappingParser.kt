package me.sqlerrorthing.parser.impl

import me.sqlerrorthing.Config
import me.sqlerrorthing.ast.Access
import me.sqlerrorthing.ast.IClass
import me.sqlerrorthing.ast.IField
import me.sqlerrorthing.ast.Named
import me.sqlerrorthing.parser.MappingParser
import me.sqlerrorthing.utils.AccessFlags

private const val CLASS_PREFIX_START = 'c'

object TinyMappingParser : MappingParser {
    private fun parseClassAttributes(
        line: List<String>,
        `class`: IClass,
    ) {
        when (line[0]) {
            "f" -> parseClassField(line, `class`)
        }
    }

    private fun parseClassField(
        line: List<String>,
        `class`: IClass,
    ) {
        val (access, final, static) = parseFieldNMethodFlags(line[3].toInt())
        val field =
            IField(
                name =
                    Named(
                        obfuscated = line.getOrNull(2),
                        mojang = line.getOrNull(7),
                        intermediary = line.getOrNull(8),
                        yarn = line.getOrNull(9),
                        searge = line.getOrNull(10),
                    ),
                structure = line.getOrNull(1) ?: "",
                access = access,
                final = final,
                static = static,
            )

        `class`.fields.add(field)
    }

    /**
     * Parses the field/method flags to determine the access level, whether it's final and whether it's static.
     *
     * @param flags The integer representation of the flags.
     * @return A Triple representing the access level, finality and staticness of the field/method.
     *         The first element of the Triple is the access level, which is an enumeration value of type Access.
     *         The second and third elements of the Triple are boolean values representing whether the field/method is final and static, respectively.
     */
    private fun parseFieldNMethodFlags(flags: Int): Triple<Access, Boolean, Boolean> {
        val access =
            when {
                AccessFlags.isPublic(flags) -> Access.PUBLIC
                AccessFlags.isPrivate(flags) -> Access.PRIVATE
                AccessFlags.isProtected(flags) -> Access.PROTECTED
                AccessFlags.isPackagePrivate(flags) -> Access.DEFAULT
                else -> TODO("Fix me, access flag not found $flags")
            }

        return Triple(
            access,
            AccessFlags.isFinal(flags),
            AccessFlags.isStatic(flags),
        )
    }

    override fun parse(config: Config): List<IClass> {
        val classes: MutableList<IClass> = mutableListOf()
        var classHolder: IClass? = null

        config.mappings.forEachLine { line ->
            if (line.startsWith(CLASS_PREFIX_START)) {
                if (classHolder != null) {
                    classes.add(classHolder!!)
                }

                classHolder = IClass()

                val parts = line.trim().split("\t")
                classHolder?.name =
                    Named(
                        obfuscated = parts.getOrNull(1),
                        mojang = parts.getOrNull(6),
                        intermediary = parts.getOrNull(7),
                        yarn = parts.getOrNull(8),
                        searge = parts.getOrNull(9),
                    )
            }

            if (line.startsWith("\t") && classHolder != null) {
                parseClassAttributes(line.trim().split("\t"), classHolder!!)
            }
        }

        return classes
    }
}
