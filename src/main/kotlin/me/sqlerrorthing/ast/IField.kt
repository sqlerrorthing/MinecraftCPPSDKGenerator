package me.sqlerrorthing.ast

class IField (
    var name: Named,
    var type: String,
    var flags: MutableList<Flags> = emptyList<Flags>().toMutableList(),
    var access: MutableList<Access> = emptyList<Access>().toMutableList(),
) : IAST