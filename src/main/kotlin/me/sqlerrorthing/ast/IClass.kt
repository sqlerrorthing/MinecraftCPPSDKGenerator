package me.sqlerrorthing.ast

data class IClass (
    var name: Named = Named(),
    var fields: List<IField> = listOf(),
    var methods: List<IMethod> = listOf()
) : IAST