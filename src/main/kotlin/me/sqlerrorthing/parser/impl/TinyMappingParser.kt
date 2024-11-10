package me.sqlerrorthing.parser.impl

import me.sqlerrorthing.Config
import me.sqlerrorthing.ast.IClass
import me.sqlerrorthing.ast.Named
import me.sqlerrorthing.parser.MappingParser
import java.io.File
import java.lang.classfile.ClassBuilder

object TinyMappingParser : MappingParser {

    override fun parse(config: Config): List<IClass> {

        val classes: MutableList<IClass> = mutableListOf()
        var classHolder: IClass? = null

        config.mappings.forEachLine { line ->
            if (line.startsWith("c")) /* class */ {
                if(classHolder != null)
                    classes.add(classHolder!!)

                classHolder = IClass()

                val parts = line.trim().split("\t")
                classHolder?.name = Named(
                    obfuscated = parts.getOrNull(1),
                    mojang = parts.getOrNull(6),
                    intermediary = parts.getOrNull(7),
                    yarn = parts.getOrNull(8),
                    searge = parts.getOrNull(9)
                )
            }
        }


        return null!!
    }
}