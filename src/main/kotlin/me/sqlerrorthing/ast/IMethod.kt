package me.sqlerrorthing.ast

class MethodParameters(
    var name: Named,
    var parameters: MutableMap<String, String>,
)

class IMethod(
    var namesAndParams: MethodParameters,
    var type: String,
    var static: Boolean,
    var `final`: Boolean,
    var access: Access = Access.PUBLIC,
)
