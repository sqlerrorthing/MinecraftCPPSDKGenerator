package me.sqlerrorthing.ast

class MethodParameters(
    var name: Named,
    var parameters: MutableMap<String, String>
)

class IMethod(
    var namesAndParams: MethodParameters,
    var type: String,
    var flags: List<Flags> = emptyList(),
    var access: List<Access> = listOf(Access.PUBLIC),
)