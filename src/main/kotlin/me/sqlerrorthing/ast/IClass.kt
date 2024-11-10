package me.sqlerrorthing.ast

data class IClass(
    var name: Named = Named(),
    var fields: MutableList<IField> = mutableListOf(),
    var methods: MutableList<IMethod> = mutableListOf(),
) : IAST
