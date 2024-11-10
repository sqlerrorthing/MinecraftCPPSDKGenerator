package me.sqlerrorthing.parser

import me.sqlerrorthing.Config
import me.sqlerrorthing.ast.IClass

interface MappingParser {
    fun parse(config: Config): List<IClass>
}
