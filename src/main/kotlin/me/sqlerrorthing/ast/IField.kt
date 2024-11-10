package me.sqlerrorthing.ast

class IField(
    var name: Named = Named(),
    var structure: String = "",
    var static: Boolean,
    var `final`: Boolean,
    var access: Access = Access.PUBLIC,
) : IAST
