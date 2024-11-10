package me.sqlerrorthing.ast

data class IClass(
    var name: Named = Named(),
    var final: Boolean = false,
    var access: Access = Access.PUBLIC,
    var fields: MutableList<IField> = mutableListOf(),
    var methods: MutableList<IMethod> = mutableListOf(),
) : IAST
