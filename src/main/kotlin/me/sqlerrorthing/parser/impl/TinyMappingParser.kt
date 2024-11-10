package me.sqlerrorthing.parser.impl

import me.sqlerrorthing.Config
import me.sqlerrorthing.ast.IClass
import me.sqlerrorthing.ast.IField
import me.sqlerrorthing.ast.Named
import me.sqlerrorthing.parser.MappingParser

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
            )

        `class`.fields.add(field)
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
