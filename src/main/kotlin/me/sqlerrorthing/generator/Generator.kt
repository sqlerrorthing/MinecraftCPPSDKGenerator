package me.sqlerrorthing.generator

import me.sqlerrorthing.Config
import me.sqlerrorthing.ast.IClass

interface Generator {

    fun generate(config: Config, parsed: List<IClass>)

}
